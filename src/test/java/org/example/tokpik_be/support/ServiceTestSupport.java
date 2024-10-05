package org.example.tokpik_be.support;

import org.example.tokpik_be.tag.repository.PlaceTagRepository;
import org.example.tokpik_be.tag.repository.TopicTagRepository;
import org.example.tokpik_be.tag.repository.UserPlaceTagRepository;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.example.tokpik_be.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@DataJpaTest
public abstract class ServiceTestSupport {

    @Autowired
    protected EntityManager em;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TopicTagRepository topicTagRepository;

    @Autowired
    protected PlaceTagRepository placeTagRepository;

    @Autowired
    protected UserTopicTagRepository userTopicTagRepository;

    @Autowired
    protected UserPlaceTagRepository userPlaceTagRepository;

    @AfterEach
    void tearDown() {
        userTopicTagRepository.deleteAllInBatch();
        userPlaceTagRepository.deleteAllInBatch();

        topicTagRepository.deleteAllInBatch();
        placeTagRepository.deleteAllInBatch();

        userRepository.deleteAllInBatch();
    }
}
