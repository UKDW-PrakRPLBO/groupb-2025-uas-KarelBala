package org.uas.util;

import java.io.*;

public class SessionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SESSION_FILE = "session.ser";

    private static SessionManager instance;
    private boolean isLoggedIn = false;

    private SessionManager() {
        loadSession();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    private void loadSession() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SESSION_FILE))) {
            SessionManager loadedSession = (SessionManager) ois.readObject();
            this.isLoggedIn = loadedSession.isLoggedIn;
        } catch (IOException | ClassNotFoundException e) {
            this.isLoggedIn = false;
        }
    }

    private void saveSession() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan sesi: " + e.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void login() {
        this.isLoggedIn = true;
        saveSession();
    }

    public void logout() {
        this.isLoggedIn = false;
        try {
            File sessionFile = new File(SESSION_FILE);
            if (sessionFile.exists()) {
                sessionFile.delete();
            }
        } catch (SecurityException e) {
            System.err.println("Gagal menghapus file sesi: " + e.getMessage());
        }
    }
}