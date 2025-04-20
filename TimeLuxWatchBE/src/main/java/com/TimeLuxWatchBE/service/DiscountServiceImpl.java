package com.TimeLuxWatchBE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TimeLuxWatchBE.entity.DiscountEntity;
import com.TimeLuxWatchBE.repository.DiscountDetailRepository;
import com.TimeLuxWatchBE.repository.DiscountRepository;
import com.TimeLuxWatchBE.service.DiscountService;

import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private DiscountDetailRepository discountDetailRepository;
    @Override
    public List<DiscountEntity> findAll() {
        return discountRepository.findAll();
    }

    @Override
    public DiscountEntity findById(int id) {
        return discountRepository.findById(id).orElse(null);
    }

    @Override
    public void save(DiscountEntity discount) {
        discountRepository.save(discount);
    }

    @Override
    @Transactional
    public void delete(int id) {
    	discountDetailRepository.deleteByDiscountId(id);
        discountRepository.deleteById(id);
    }
}
