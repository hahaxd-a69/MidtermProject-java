package co.istad.gym.dao;

import co.istad.gym.model.Member;
import java.util.List;
import java.util.Optional;

public interface MemberDAO {
    // CREATE
    int addMember(Member member) throws Exception;

    // READ
    List<Member> getAllMembers() throws Exception;
    Optional<Member> getMemberById(int memberId) throws Exception;
    Optional<Member> getMemberByEmail(String email) throws Exception;
    List<Member> getActiveMembers() throws Exception;

    // UPDATE
    boolean updateMember(Member member) throws Exception;

    // DELETE
    boolean softDeleteMember(int memberId) throws Exception;

    // SEARCH
    List<Member> searchMembers(String keyword) throws Exception;

    // STATISTICS
    int countMembers() throws Exception;
    int countActiveMembers() throws Exception;
}