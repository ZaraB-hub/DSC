package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripReceiptDAO;

import javax.persistence.EntityManager;

public class TripReceiptDAO implements ITripReceiptDAO {
    private final EntityManager em;

    public TripReceiptDAO(EntityManager em) {
        this.em = em;
    }
}
