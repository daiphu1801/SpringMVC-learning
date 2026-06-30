package com.examp.springmvc.user.infrastructure.persistence;

import com.examp.springmvc.user.application.address.query.AddressDTO;
import com.examp.springmvc.user.application.address.query.UserAddressQueryPort;
import com.examp.springmvc.user.infrastructure.mapper.UserAddressMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserAddressQueryAdapter implements UserAddressQueryPort {

    private final UserAddressMapper userAddressMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UserAddressQueryAdapter(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    @Override
    public List<AddressDTO> findByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
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
