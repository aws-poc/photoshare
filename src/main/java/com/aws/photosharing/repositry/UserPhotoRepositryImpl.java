package com.aws.photosharing.repositry;

import com.aws.photosharing.entity.UserPhoto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UserPhotoRepositryImpl implements CustomUserPhotoRepositry {

    private final EntityManager entityManager;

    public UserPhotoRepositryImpl(@Autowired EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<UserPhoto> findByTags(String userName, String[] tags) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UserPhoto> query = builder.createQuery(UserPhoto.class);
        final Root<UserPhoto> root = query.from(UserPhoto.class);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("userName"), userName));
        Predicate tagPredicate = null;
        for (String tag: tags) {
            Predicate p = builder.like(root.get("tags"), "%" + tag + "%");
            if (tagPredicate == null) {
                tagPredicate = p;
            } else {
                tagPredicate = builder.or(tagPredicate, p);
            }
        }
        predicates.add(tagPredicate);

        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        final TypedQuery<UserPhoto> typedQuery = entityManager.createQuery(query);

        return typedQuery.getResultList();
    }
}
