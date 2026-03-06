package com.webgara.module.inventory.service;

import com.webgara.module.inventory.dto.PartRequest;
import com.webgara.module.inventory.dto.PartResponse;
import com.webgara.module.inventory.model.PartCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartService {
    PartResponse create(PartRequest request);

    PartResponse getById(String id);

    PartResponse update(String id, PartRequest request);

    void delete(String id);

    Page<PartResponse> listByGarage(String garageId, Pageable pageable);

    Page<PartResponse> listByCategory(String garageId, PartCategory category, Pageable pageable);

    Page<PartResponse> searchByName(String name, Pageable pageable);

    List<PartResponse> getLowStockParts(String garageId);
}
