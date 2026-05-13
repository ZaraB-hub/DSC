package dst.ass2.service.auth.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.ICachingAuthenticationService;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
@Named
@Transactional
public class CachingAuthenticationService implements ICachingAuthenticationService {

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<String, byte[]> userToPassword = new ConcurrentHashMap<>();
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();
    private ReadWriteLock userToPassLock = new ReentrantReadWriteLock();

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        userToPassLock.readLock().lock();
        try {
            byte[] storedPassword = getCachedOrLoadPassword(email);
            if (!Arrays.equals(storedPassword, hash(password))) {
                throw new AuthenticationException("Invalid password");
            }

            String token = UUID.randomUUID().toString();
            tokenToUser.put(token, email);
            return token;
        } finally {
            userToPassLock.readLock().unlock();
        }
    }

    @Override
    public void changePassword(String email, String newPassword) throws NoSuchUserException {
        userToPassLock.writeLock().lock();
        try {
            byte[] hashedPassword = sha1(newPassword);
            int updated = entityManager.createQuery("update Rider r set r.password = :password where r.email = :email")
                    .setParameter("password", hashedPassword)
                    .setParameter("email", email)
                    .executeUpdate();

            if (updated == 0) {
                throw new NoSuchUserException("No user found for email " + email);
            }

            userToPassword.put(email, hashedPassword);
        } finally {
            userToPassLock.writeLock().unlock();
        }
    }

    @Override
    public String getUser(String token) {
        return tokenToUser.get(token);
    }

    @Override
    public boolean isValid(String token) {
        return tokenToUser.containsKey(token);
    }

    @Override
    public boolean invalidate(String token) {
        return tokenToUser.remove(token) != null;
    }

    @Override
    @PostConstruct
    public void loadData() {
        clearCache();

        List<Object[]> users = entityManager.createQuery(
                        "select r.email, r.password from Rider r",
                        Object[].class)
                .getResultList();
        for (Object[] user : users) {
            userToPassword.put((String) user[0], (byte[]) user[1]);
        }
    }

    @Override
    public void clearCache() {
        userToPassword.clear();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public void setUserToPassLock(ReadWriteLock readWriteLock) {
        this.userToPassLock = readWriteLock;
    }

    private byte[] getCachedOrLoadPassword(String email) throws NoSuchUserException {
        byte[] password = userToPassword.get(email);
        if (password != null) {
            return password;
        }

        try {
            password = entityManager.createQuery(
                            "select r.password from Rider r where r.email = :email",
                            byte[].class)
                    .setParameter("email", email)
                    .getSingleResult();
            userToPassword.put(email, password);
            return password;
        } catch (NoResultException e) {
            throw new NoSuchUserException("No user found for email " + email, e);
        }
    }

    private byte[] hash(String password) throws AuthenticationException {
        try {
            return sha1(password);
        } catch (RuntimeException e) {
            throw new AuthenticationException("Could not hash password", e);
        }
    }

    private byte[] sha1(String password) {
        try {
            return MessageDigest.getInstance("SHA1").digest(password.getBytes());
        } catch (Exception e) {
            throw new IllegalStateException("SHA1 digest is not available", e);
        }
    }
}
