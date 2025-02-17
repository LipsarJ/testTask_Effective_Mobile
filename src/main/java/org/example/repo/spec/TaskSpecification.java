package org.example.repo.spec;

import jakarta.persistence.criteria.Join;
import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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

    public static Specification<Task> searchByFilterUser(List<String> usernames) {
        return (root, query, cb) -> {
            if (usernames == null || usernames.isEmpty()) {
                return cb.conjunction();
            }

            Join<Task, User> creatorJoin = root.join("creator");
            var creatorPredicate = creatorJoin.get("username").in(usernames);

            Join<Task, User> developersJoin = root.join("developers");
            var developersPredicate = developersJoin.get("username").in(usernames);

            return cb.or(creatorPredicate, developersPredicate);
        };
    }

}
