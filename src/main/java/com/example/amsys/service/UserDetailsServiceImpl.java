package com.example.amsys.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.amsys.model.User;
import com.example.amsys.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーIDがありません。 ユーザーID:" + userid));

        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getHashedPassword(),
                Collections.emptyList()
        );
    }

}
