package com.odc.om.paie.services;

import com.odc.om.paie.web.mobile.dto.TransactionRequestDTO;
import com.odc.om.paie.entities.Transaction;

public interface TransactionStrategy {
    Transaction execute(TransactionRequestDTO request);
}