package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IRider;

public interface IRiderDAO extends GenericDAO<IRider> {

    /**
     * Returns the rider associated with the given email. Returns null if the email does not exist.
     *
     * @param email the email address
     * @return the rider or null
     */
    IRider findByEmail(String email);
}
