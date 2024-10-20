package com.noah.jwt.service;

import com.noah.jwt.db.User;
import com.noah.jwt.db.repository.UserRepository;
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
public class UserManager implements UserDetailsManager {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void createUser(UserDetails user) {
    ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));
    User test = userRepository.save((User) user);
  }

  @Override
  public void updateUser(UserDetails user) {
    userRepository.save((User) user);
  }

  @Override
  public void deleteUser(String username) {
    userRepository.deleteUserByUsername(username);
  }

  public void changePasswordById(Long id, String oldPassword, String newPassword) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException(
                    MessageFormat.format("ID {0} not found for any user", id)
            ));
    changePassword(user, oldPassword, newPassword);
  }

  public void changePasswordByUsername(String username, String oldPassword, String newPassword) {
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
    return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(
            MessageFormat.format("ID {0} not found for any user", id)
    ));
  }
}
