package com.application.domain.repository;

import com.application.domain.entity.Member;

import java.util.List;

public interface MemberRepository {
    public void save(Member member);
    public Member findById(Long id);
    public Member findByCreadentialId(String credentialId);
    public Member findByEmail(String email);
    public List<Member> findAll();
    public void deleteById(Long id);
    public void clear();
    public void update(Member member);
}
