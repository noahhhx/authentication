package com.noah.jwt.service;

import com.noah.jwt.entities.User;
import com.noah.jwt.exceptions.UserNameNotUniqueException;
import com.noah.jwt.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsManager {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void createUser(UserDetails userDetails) {
    if (!(userDetails instanceof User user)) {
      throw new IllegalArgumentException("User must be an instance of User class");
    }
    if (userExists(user.getUsername())) {
      throw new UserNameNotUniqueException(
          MessageFormat.format("Username {0} already exists", user.getUsername())
      );
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  @Override
  public void updateUser(UserDetails user) {
    // TODO
    userRepository.save((User) user);
  }

  @Override
  public void deleteUser(String username) {
    // TODO
    userRepository.deleteUserByUsername(username);
  }

  public void changePasswordById(Long id, String oldPassword, String newPassword) {
    // TODO
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException(
                    MessageFormat.format("ID {0} not found for any user", id)
            ));
    changePassword(user, oldPassword, newPassword);
  }

  public void changePasswordByUsername(String username, String oldPassword, String newPassword) {
    // TODO
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                    MessageFormat.format("Username {0} not found", username)
            ));
    changePassword(user, oldPassword, newPassword);
  }

  public void changePassword(User user, String oldPassword, String newPassword) {
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new BadCredentialsException("Old password doesn't match");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Override
  public void changePassword(String oldPassword, String newPassword) {
    // TODO don't we need something that identifies the user to be able to update the password?
  }

  @Override
  public boolean userExists(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            MessageFormat.format("Username {0} not found", username)
        ));
  }

  public User findById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException(
            MessageFormat.format("ID {0} not found for any user", id)
        ));
  }
}
