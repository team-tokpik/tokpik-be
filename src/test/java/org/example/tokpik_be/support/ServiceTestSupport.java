package org.example.tokpik_be.support;

import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.scrap.repository.ScrapTopicRepository;
import jakarta.persistence.EntityManager;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.type.repository.UserPlaceTypeRepository;
import org.example.tokpik_be.type.repository.UserTopicTypeRepository;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest

public abstract class ServiceTestSupport {

    @Autowired
    protected EntityManager em;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TopicTypeRepository topicTypeRepository;

    @Autowired
    protected PlaceTypeRepository placeTypeRepository;

    @Autowired
    protected UserTopicTypeRepository userTopicTypeRepository;

    @Autowired
    protected UserPlaceTypeRepository userPlaceTypeRepository;

    @Autowired
    protected ScrapRepository scrapRepository;

    @Autowired
    protected ScrapTopicRepository scrapTopicRepository;

    @Autowired
    protected TalkTopicRepository talkTopicRepository;

    @AfterEach
    void tearDown() {
        scrapTopicRepository.deleteAllInBatch();
        scrapRepository.deleteAllInBatch();
        talkTopicRepository.deleteAllInBatch();

        userTopicTypeRepository.deleteAllInBatch();
        userPlaceTypeRepository.deleteAllInBatch();

        topicTypeRepository.deleteAllInBatch();
        placeTypeRepository.deleteAllInBatch();

        userRepository.deleteAllInBatch();
    }
}
