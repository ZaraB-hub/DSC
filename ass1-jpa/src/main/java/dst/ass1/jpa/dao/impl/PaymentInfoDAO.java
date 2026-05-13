package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IPaymentInfoDAO;

import javax.persistence.EntityManager;

public class PaymentInfoDAO implements IPaymentInfoDAO {
    private final EntityManager em;

    public PaymentInfoDAO(EntityManager em) {
        this.em = em;
    }
}
