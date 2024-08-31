package com.dev.servlet.mapper;

import com.dev.servlet.dto.InventoryDto;
import com.dev.servlet.pojo.Inventory;

public final class InventoryMapper {
    private InventoryMapper() {
    }


    /**
     * {@link Inventory} from {@link InventoryDto}
     *
     * @param dto
     * @return {@link Inventory}
     */
    public static Inventory from(InventoryDto dto) {
        Inventory inventory = new Inventory(dto.getId());
        inventory.setQuantity(dto.getQuantity());
        inventory.setStatus(dto.getStatus());
        inventory.setProduct(ProductMapper.from(dto.getProduct()));
        inventory.setUser(UserMapper.from(dto.getUser()));
        return inventory;
    }

    /**
     * {@link InventoryDto} from {@link Inventory}
     *
     * @param inventory
     * @return {@link InventoryDto}
     */
    public static InventoryDto from(Inventory inventory) {
        InventoryDto dto = new InventoryDto(inventory.getId());
        dto.setQuantity(inventory.getQuantity());
        dto.setStatus(inventory.getStatus());
        dto.setDescription(inventory.getDescription());
        dto.setProduct(ProductMapper.from(inventory.getProduct()));
        dto.setUser(UserMapper.onlyId(inventory.getUser()));
        return dto;
    }
}
