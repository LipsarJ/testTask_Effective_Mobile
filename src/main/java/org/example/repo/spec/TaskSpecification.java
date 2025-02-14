package org.example.repo.spec;

import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> searchByFilterText(String filterText) {
        return (root, query, criteriaBuilder) -> {
            if (filterText == null || filterText.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String filterTextLowerCase = "%" + filterText.toLowerCase() + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    filterTextLowerCase
            );
        };
    }

    public static Specification<Task> searchByFilterUser(User filterUser) {
        return (root, query, cb) -> {
            if (filterUser == null) {
                return cb.conjunction();
            }

            return cb.or(
                    cb.equal(root.get("creator"), filterUser),
                    cb.isMember(filterUser, root.get("developers"))
            );
        };
    }
}
