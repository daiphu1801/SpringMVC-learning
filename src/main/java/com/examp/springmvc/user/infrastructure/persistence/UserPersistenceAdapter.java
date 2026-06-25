package com.examp.springmvc.user.infrastructure.persistence;

import com.examp.springmvc.shared.domain.DomainEvent;
import com.examp.springmvc.user.domain.model.Address;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import com.examp.springmvc.user.infrastructure.mapper.UserAddressMapper;
import com.examp.springmvc.user.infrastructure.mapper.UserCommandMapper;
import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserCommandMapper userCommandMapper;
    private final UserQueryMapper userQueryMapper;
    private final UserDataAccessMapper userDataAccessMapper;
    private final UserAddressMapper userAddressMapper;
    private final ApplicationEventPublisher eventPublisher;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UserPersistenceAdapter(
            UserCommandMapper userCommandMapper,
            UserQueryMapper userQueryMapper,
            UserDataAccessMapper userDataAccessMapper,
            UserAddressMapper userAddressMapper,
            ApplicationEventPublisher eventPublisher) {
        this.userCommandMapper = userCommandMapper;
        this.userQueryMapper = userQueryMapper;
        this.userDataAccessMapper = userDataAccessMapper;
        this.userAddressMapper = userAddressMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        UserDbEntity entity = userQueryMapper.findById(id);
        User user = userDataAccessMapper.toDomain(entity);
        populateAddresses(user);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        UserDbEntity entity = userQueryMapper.findByUsername(username);
        User user = userDataAccessMapper.toDomain(entity);
        populateAddresses(user);
        return Optional.ofNullable(user);
    }

    private void populateAddresses(User user) {
        if (user != null && user.getId() != null) {
            List<UserAddressDbEntity> addressEntities = userAddressMapper.findByUserId(user.getId());
            List<Address> addresses = new ArrayList<>();
            for (UserAddressDbEntity entity : addressEntities) {
                addresses.add(userDataAccessMapper.toAddressDomain(entity));
            }
            user.setAddresses(addresses);
        }
    }

    @Override
    public void save(User user) {
        if (user == null) {
            return;
        }
        UserDbEntity dbEntity = userDataAccessMapper.toDbEntity(user);
        if (dbEntity.getId() == null) {
            userCommandMapper.insert(dbEntity);
            user.setId(dbEntity.getId());
        } else {
            userCommandMapper.update(dbEntity);
        }

        // Cascade Save Addresses
        Long userId = user.getId();
        List<Address> domainAddresses = user.getAddresses();
        List<Long> activeIds = new ArrayList<>();

        for (Address address : domainAddresses) {
            UserAddressDbEntity addrEntity = userDataAccessMapper.toAddressDbEntity(address, userId);
            if (addrEntity.getId() == null) {
                userAddressMapper.insert(addrEntity);
            } else {
                userAddressMapper.update(addrEntity);
            }
            activeIds.add(addrEntity.getId());
        }

        // Delete removed addresses
        userAddressMapper.deleteByUserIdExceptIds(userId, activeIds);

        // Publish events after successful save
        List<DomainEvent> events = new ArrayList<>(user.getDomainEvents());
        user.clearDomainEvents();
        for (DomainEvent event : events) {
            eventPublisher.publishEvent(event);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }
        userCommandMapper.deleteById(id);
    }
}
