package com.my.fomo.websocket.domain;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SubscriptionRegistry {

    // symbol → set of sessionIds
    private final Map<String, Set<String>> symbolSessions = new ConcurrentHashMap<>();
    // symbol → excd (exchange code)
    private final Map<String, String> symbolExcd = new ConcurrentHashMap<>();
    // sessionId → set of symbols
    private final Map<String, Set<String>> sessionSymbols = new ConcurrentHashMap<>();
    // "sessionId:subscriptionId" → symbol
    private final Map<String, String> subIdToSymbol = new ConcurrentHashMap<>();

    /**
     * @return true if this is the first subscriber for the symbol
     */
    public boolean subscribe(String sessionId, String subscriptionId, String symbol, String excd) {
        subIdToSymbol.put(key(sessionId, subscriptionId), symbol);
        sessionSymbols.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(symbol);
        symbolExcd.putIfAbsent(symbol, excd);

        Set<String> sessions = symbolSessions.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet());
        boolean wasEmpty = sessions.isEmpty();
        sessions.add(sessionId);
        return wasEmpty;
    }

    /**
     * @return symbol if found; empty if subscriptionId unknown
     */
    public Optional<String> unsubscribe(String sessionId, String subscriptionId) {
        String symbol = subIdToSymbol.remove(key(sessionId, subscriptionId));
        if (symbol == null) return Optional.empty();

        removeSessionFromSymbol(sessionId, symbol);
        return Optional.of(symbol);
    }

    /**
     * Remove all subscriptions for a disconnected session.
     * @return symbols that now have no subscribers
     */
    public Set<String> disconnect(String sessionId) {
        Set<String> symbols = sessionSymbols.remove(sessionId);
        if (symbols == null) return Collections.emptySet();

        Set<String> orphaned = ConcurrentHashMap.newKeySet();
        for (String symbol : symbols) {
            Set<String> sessions = symbolSessions.get(symbol);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    symbolSessions.remove(symbol);
                    symbolExcd.remove(symbol);
                    orphaned.add(symbol);
                }
            }
        }
        subIdToSymbol.entrySet().removeIf(e -> e.getKey().startsWith(sessionId + ":"));
        return orphaned;
    }

    public boolean hasSubscribers(String symbol) {
        Set<String> sessions = symbolSessions.get(symbol);
        return sessions != null && !sessions.isEmpty();
    }

    public Optional<String> getExcd(String symbol) {
        return Optional.ofNullable(symbolExcd.get(symbol));
    }

    private void removeSessionFromSymbol(String sessionId, String symbol) {
        Set<String> sessions = symbolSessions.get(symbol);
        if (sessions == null) return;
        sessions.remove(sessionId);
        if (sessions.isEmpty()) {
            symbolSessions.remove(symbol);
            symbolExcd.remove(symbol);
        }
        Set<String> symbols = sessionSymbols.get(sessionId);
        if (symbols != null) symbols.remove(symbol);
    }

    private String key(String sessionId, String subscriptionId) {
        return sessionId + ":" + subscriptionId;
    }
}
