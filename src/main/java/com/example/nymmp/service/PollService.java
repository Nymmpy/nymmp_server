package com.example.nymmp.service;

import com.example.nymmp.dto.poll.*;
import com.example.nymmp.model.*;
import com.example.nymmp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollJPARepository pollRepository;

    @Autowired
    private UserJPARepository userRepository;

    @Autowired
    private PollOptionJPARepository pollOptionRepository;

    @Autowired
    private GroupJPARepository groupRepository;

    @Autowired
    private QuestionJPARepository questionRepository;

    @Autowired
    private PollResultJPARepository pollResultRepository;

    public PollResponse getPollById(Long pollId, Long groupId) {
        // Poll 조회
        Poll poll = pollRepository.findById(pollId).orElseThrow();
        Question question = poll.getQuestion();
        Long questionId = question.getQuestionId();

        // 해당 그룹과 질문에 대한 PollResult 조회
        List<PollResult> pollResults = pollResultRepository.findByGroupIdAndQuestionId(groupId, questionId);

        // 총 투표 수 (total_count) 계산
        int totalCount = pollResults.size();

        // 각 옵션별 선택 횟수 (vote_count) 계산
        Map<Long, Integer> voteCountMap = new HashMap<>();
        for (PollResult result : pollResults) {
            Long choiceUserId = result.getChoice().getUserId();
            voteCountMap.merge(choiceUserId, 1, Integer::sum);
        }

        // PollOption 조회 및 옵션 리스트 구성
        PollOption pollOption = pollOptionRepository.findByPoll_PollId(pollId).orElseThrow();
        List<PollOptionResponse> options = Arrays.asList(
                new PollOptionResponse(pollOption.getOption1().getUserId(), pollOption.getOption1().getUsername(), voteCountMap.getOrDefault(pollOption.getOption1().getUserId(), 0)),
                new PollOptionResponse(pollOption.getOption2().getUserId(), pollOption.getOption2().getUsername(), voteCountMap.getOrDefault(pollOption.getOption2().getUserId(), 0)),
                new PollOptionResponse(pollOption.getOption3().getUserId(), pollOption.getOption3().getUsername(), voteCountMap.getOrDefault(pollOption.getOption3().getUserId(), 0)),
                new PollOptionResponse(pollOption.getOption4().getUserId(), pollOption.getOption4().getUsername(), voteCountMap.getOrDefault(pollOption.getOption4().getUserId(), 0))
        );

        // 그룹 내에서 가장 많은 득표를 한 사용자 3명 조회
        List<TopVoterResponse> topVoters = pollResults.stream()
                .collect(Collectors.groupingBy(result -> result.getChoice().getUserId(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> {
                    User user = userRepository.findById(entry.getKey()).orElseThrow();
                    return new TopVoterResponse(user.getUserId(), user.getUsername());
                })
                .collect(Collectors.toList());

        return new PollResponse(poll.getPollId(), question.getQuestionText(), totalCount, options, topVoters);
    }

    public List<PollOptionResponse> shuffleOptions(Long pollId, Long groupId) {
        // 그룹 내의 모든 사용자 조회
        List<User> users = userRepository.findByGroupId(groupId);
        Collections.shuffle(users);
        List<User> selectedUsers = users.subList(0, 4);

        // PollOption 업데이트
        Poll poll = pollRepository.findById(pollId).orElseThrow();
        PollOption pollOption = pollOptionRepository.findByPoll_PollId(pollId).orElseThrow();
        pollOption.setOption1(selectedUsers.get(0));
        pollOption.setOption2(selectedUsers.get(1));
        pollOption.setOption3(selectedUsers.get(2));
        pollOption.setOption4(selectedUsers.get(3));
        pollOptionRepository.save(pollOption);

        // 해당 그룹과 질문에 대한 PollResult 조회
        Question question = poll.getQuestion();
        Long questionId = question.getQuestionId();
        List<PollResult> pollResults = pollResultRepository.findByGroupIdAndQuestionId(groupId, questionId);

        // 각 옵션별 선택 횟수 (voteCount) 계산
        Map<Long, Integer> voteCountMap = new HashMap<>();
        for (PollResult result : pollResults) {
            Long choiceUserId = result.getChoice().getUserId();
            voteCountMap.merge(choiceUserId, 1, Integer::sum);
        }

        // 새로운 옵션 리스트 반환
        return Arrays.asList(
                new PollOptionResponse(selectedUsers.get(0).getUserId(), selectedUsers.get(0).getUsername(), voteCountMap.getOrDefault(selectedUsers.get(0).getUserId(), 0)),
                new PollOptionResponse(selectedUsers.get(1).getUserId(), selectedUsers.get(1).getUsername(), voteCountMap.getOrDefault(selectedUsers.get(1).getUserId(), 0)),
                new PollOptionResponse(selectedUsers.get(2).getUserId(), selectedUsers.get(2).getUsername(), voteCountMap.getOrDefault(selectedUsers.get(2).getUserId(), 0)),
                new PollOptionResponse(selectedUsers.get(3).getUserId(), selectedUsers.get(3).getUsername(), voteCountMap.getOrDefault(selectedUsers.get(3).getUserId(), 0))
        );
    }

    public VoteResponse submitVote(PollVoteRequest voteRequest, Long groupId) {
        // Poll 조회
        Poll poll = pollRepository.findById(voteRequest.getPollId()).orElseThrow();
        // User 조회 (투표한 사람)
        User voter = userRepository.findById(voteRequest.getUserId()).orElseThrow();

        // User 조회 (투표에서 선택된 사람)
        User selectedUser = userRepository.findById(voteRequest.getSelected()).orElseThrow();

        // poll 에 쓰인 optionId추출
        PollOption usedOption = pollOptionRepository.findByPoll_PollId(poll.getPollId()).orElseThrow();

        // 새로운 PollResult 생성 및 저장
        PollResult pollResult = new PollResult();
        pollResult.setPoll(poll);
        pollResult.setChoice(voter);
        pollResult.setOption(usedOption); // 선택된 사용자를 옵션으로 설정
        pollResultRepository.save(pollResult);

        boolean isLast = voteRequest.getIsLast();
        Long nextPollId = null;
        if (!isLast) {
            nextPollId = getNextPollId(voteRequest.getUserId(), voter.getGroup().getGroupId());
        }

        return new VoteResponse(true, "Vote successful", nextPollId);
    }

    public Long getNextPollId(Long userId, Long groupId) {
        // 해당 그룹과 사용자로 조회한 question_id를 제외한 나머지 question_id 중 가장 작은 값을 찾음
        List<PollResult> userResults = pollResultRepository.findByGroupIdAndUserId(groupId, userId);
        Set<Long> answeredQuestionIds = userResults.stream()
                .map(result -> result.getPoll().getQuestion().getQuestionId())
                .collect(Collectors.toSet());

        List<Question> allQuestions = questionRepository.findAll();
        Optional<Question> nextQuestion = allQuestions.stream()
                .filter(question -> !answeredQuestionIds.contains(question.getQuestionId()))
                .min(Comparator.comparingLong(Question::getQuestionId));

        if (nextQuestion.isPresent()) {
            // 새로운 Poll 생성 및 저장
            Poll newPoll = new Poll();
            newPoll.setQuestion(nextQuestion.get());
            newPoll.setGroup(groupRepository.findById(groupId).orElseThrow());
            pollRepository.save(newPoll);

            // PollOption 생성 및 저장
            List<User> users = userRepository.findByGroupId(groupId);
            Collections.shuffle(users);
            List<User> selectedUsers = users.subList(0, 4);

            PollOption pollOption = new PollOption();
            pollOption.setPoll(newPoll);
            pollOption.setOption1(selectedUsers.get(0));
            pollOption.setOption2(selectedUsers.get(1));
            pollOption.setOption3(selectedUsers.get(2));
            pollOption.setOption4(selectedUsers.get(3));
            pollOptionRepository.save(pollOption);

            return newPoll.getPollId();
        }

        return null; // 더 이상 투표할 질문이 없을 때 처리
    }
}
