package com.application.domain.repository;

import com.application.domain.entity.Member;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class MemMemberRepository implements MemberRepository{

    private static Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;

    @Override
    public void save(Member member) {
        store.put(++sequence, member);
    }

    @Override
    public Member findById(Long id) {
        return null;
    }

    @Override
    public Member findByCreadentialId(String credentialId) {
        List<Member> members = findAll();
        for (Member member : members) {
            if(member.getCreadentialId().equals(credentialId))
                return member;
        }
        return null;
    }

    @Override
    public Member findByEmail(String email) {
        List<Member> members = findAll();
        for (Member member : members) {
            if(member.getEmail().equals(email))
                return member;
        }
        //CHECKME
        return null;
    }

    @Override
    public List<Member> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
    }

    public void update(Member member){
        //update 쿼리
    }
}
