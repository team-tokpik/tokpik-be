package org.example.tokpik_be.user.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.example.tokpik_be.tag.repository.PlaceTagRepository;
import org.example.tokpik_be.tag.repository.TopicTagRepository;
import org.example.tokpik_be.tag.repository.UserPlaceTagRepository;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.dto.request.UserUpdateNotificationTokenRequest;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final TopicTagRepository topicTagRepository;
    private final PlaceTagRepository placeTagRepository;
    private final UserRepository userRepository;
    private final UserTopicTagRepository userTopicTagRepository;
    private final UserPlaceTagRepository userPlaceTagRepository;

    private final UserQueryService userQueryService;

    public User save(User user) {

        return userRepository.save(user);
    }

    public void makeProfile(long userId, UserMakeProfileRequest request) {
        User user = userQueryService.findById(userId);
        Gender gender = Gender.from(request.gender());
        user.updateProfile(request.birth(), gender);

        updateUserTopicTags(user, request.topicTagIds());
        updateUserPlaceTags(user, request.placeTagIds());
    }

    private void updateUserTopicTags(User user, List<Long> topicTagIds) {
        if (Objects.isNull(topicTagIds) || topicTagIds.isEmpty()) {

            return;
        }

        long userId = user.getId();
        List<TopicTag> topicTags = topicTagRepository.findAllById(topicTagIds);
        if (topicTags.isEmpty()) {

            return;
        }

        List<UserTopicTag> userTopicTags = topicTags.stream()
            .map(topicTag -> new UserTopicTag(userId, topicTag))
            .toList();
        userTopicTagRepository.deleteByUserId(userId);
        userTopicTagRepository.saveAll(userTopicTags);
        user.updateUserTopicTags(userTopicTags);
    }

    private void updateUserPlaceTags(User user, List<Long> placeTagIds) {
        if (Objects.isNull(placeTagIds) || placeTagIds.isEmpty()) {

            return;
        }

        long userId = user.getId();
        List<PlaceTag> placeTags = placeTagRepository.findAllById(placeTagIds);
        if (placeTags.isEmpty()) {

            return;
        }

        List<UserPlaceTag> userPlaceTags = placeTags.stream()
            .map(placeTag -> new UserPlaceTag(userId, placeTag))
            .toList();
        userPlaceTagRepository.deleteByUserId(userId);
        userPlaceTagRepository.saveAll(userPlaceTags);
        user.updateUserPlaceTags(userPlaceTags);
    }

    public void updateNotificationToken(long userId, UserUpdateNotificationTokenRequest request) {
        User user = userQueryService.findById(userId);

        String notificationToken = request.notificationToken();
        user.updateNotificationToken(notificationToken);
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = userQueryService.findById(userId);

        userRepository.delete(user);
    }
}
