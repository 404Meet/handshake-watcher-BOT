package com.meet.handshake;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class StateStore {

    private static final File FILE =
            new File(System.getProperty("user.home"), ".handshake_state.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static class State {
        public int lastCount;
    }

    public static State load() {
        if (!FILE.exists()) {
            State s = new State();
            s.lastCount = 0;
            return s;
        }

        try {
            return MAPPER.readValue(FILE, State.class);
        } catch (IOException e) {
            e.printStackTrace();
            State s = new State();
            s.lastCount = 0;
            return s;
        }
    }

    public static void save(State state) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(FILE, state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
