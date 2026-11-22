package com.hackaton.auth.user.service.reader;

import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserPage;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.dto.UserSearchCriteria;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import com.hackaton.auth.user.repository.UserSpecifications;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReaderService implements UserReader {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "createdAt", "id");

    private final UserAccountRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll(SORT).stream().map(userMapper::toResponse).toList();
    }

    @Override
    public UserPage search(UserSearchCriteria criteria) {
        Specification<UserAccount> spec = UserSpecifications.matching(criteria);
        if (criteria.page() == null && criteria.size() == null) {
            List<UserResponse> all = userRepository.findAll(spec, SORT).stream().map(userMapper::toResponse).toList();
            return new UserPage(all, all.size());
        }
        int size = criteria.size() == null ? DEFAULT_SIZE : Math.min(Math.max(criteria.size(), 1), MAX_SIZE);
        Page<UserAccount> page = userRepository.findAll(spec, PageRequest.of(criteria.page() == null ? 0 : Math.max(criteria.page(), 0), size, SORT));
        return new UserPage(page.getContent().stream().map(userMapper::toResponse).toList(), page.getTotalElements());
    }

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Compte", id));
    }
}
