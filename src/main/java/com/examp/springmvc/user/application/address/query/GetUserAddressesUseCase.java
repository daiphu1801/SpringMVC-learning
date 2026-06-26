package com.examp.springmvc.user.application.address.query;

import com.examp.springmvc.user.infrastructure.mapper.UserAddressMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserAddressDbEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetUserAddressesUseCase implements GetUserAddressesInputPort {

    private final UserAddressMapper userAddressMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public GetUserAddressesUseCase(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> execute(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được null");
        }
        List<UserAddressDbEntity> entities = userAddressMapper.findByUserId(userId);
        List<AddressDTO> dtos = new ArrayList<>();
        for (UserAddressDbEntity entity : entities) {
            dtos.add(new AddressDTO(
                    entity.getId(),
                    entity.getReceiverName(),
                    entity.getReceiverPhone(),
                    entity.getProvince(),
                    entity.getDistrict(),
                    entity.getWard(),
                    entity.getStreetDetail(),
                    entity.isDefault()));
        }
        return dtos;
    }
}
