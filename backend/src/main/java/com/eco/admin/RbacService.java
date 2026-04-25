package com.eco.admin;

import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RbacService {

  public boolean can(String userRole, String permission) {
    return switch (userRole) {
      case "ROLE_ADMIN" -> true;
      case "ROLE_STAFF" -> Set.of("ORDER_READ", "ORDER_WRITE", "PRODUCT_READ").contains(permission);
      default -> false;
    };
  }
}
