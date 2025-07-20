# JpaRepository와 CrudRepository의 차이점과 관계

## 개요

Spring Data JPA에서는 데이터베이스 작업을 위한 다양한 Repository 인터페이스를 제공합니다. 그 중 `CrudRepository`와 `JpaRepository`가 가장 널리 사용되는 인터페이스입니다.

## Repository 계층 구조

```
Repository (marker interface)
    ↓
CrudRepository<T, ID>
    ↓
PagingAndSortingRepository<T, ID>
    ↓
JpaRepository<T, ID>
```

## CrudRepository

### 정의
`CrudRepository`는 Spring Data의 핵심 인터페이스로, 기본적인 CRUD(Create, Read, Update, Delete) 작업을 위한 메서드를 제공합니다.

### 제공되는 메서드

```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    // Create & Update
    <S extends T> S save(S entity);
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
    
    // Read
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    Iterable<T> findAll();
    Iterable<T> findAllById(Iterable<ID> ids);
    long count();
    
    // Delete
    void deleteById(ID id);
    void delete(T entity);
    void deleteAllById(Iterable<? extends ID> ids);
    void deleteAll(Iterable<? extends T> entities);
    void deleteAll();
}
```

### 사용 예제

```java
@Repository
public interface MemberCrudRepository extends CrudRepository<Member, Long> {
    // 기본 CRUD 메서드 자동 제공
    
    // 커스텀 쿼리 메서드
    Optional<Member> findByEmail(String email);
    List<Member> findByIsActiveTrue();
    List<Member> findByRole(Member.MemberRole role);
}

// 사용 예시
@Service
public class MemberCrudService {
    @Autowired
    private MemberCrudRepository memberRepository;
    
    public void crudOperations() {
        // Create
        Member member = new Member();
        member.setName("김철수");
        member.setEmail("kim@example.com");
        Member savedMember = memberRepository.save(member);
        
        // Read
        Optional<Member> foundMember = memberRepository.findById(1L);
        Iterable<Member> allMembers = memberRepository.findAll();
        long count = memberRepository.count();
        
        // Update
        if (foundMember.isPresent()) {
            Member existingMember = foundMember.get();
            existingMember.setName("김철수 수정");
            memberRepository.save(existingMember);
        }
        
        // Delete
        memberRepository.deleteById(1L);
        memberRepository.deleteAll();
    }
}
```

## JpaRepository

### 정의
`JpaRepository`는 `CrudRepository`를 확장한 인터페이스로, JPA 관련 추가 기능과 배치 작업, 페이징, 정렬 등의 고급 기능을 제공합니다.

### 제공되는 추가 메서드

```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, CrudRepository<T, ID> {
    // 배치 작업
    void flush();
    <S extends T> S saveAndFlush(S entity);
    <S extends T> List<S> saveAllAndFlush(Iterable<S> entities);
    void deleteAllInBatch(Iterable<T> entities);
    void deleteAllByIdInBatch(Iterable<ID> ids);
    void deleteAllInBatch();
    
    // 참조 관련
    T getById(ID id);  // @Deprecated, use getReferenceById
    T getReferenceById(ID id);
    
    // 향상된 조회
    List<T> findAll();
    List<T> findAll(Sort sort);
    List<T> findAllById(Iterable<ID> ids);
    
    // 페이징
    Page<T> findAll(Pageable pageable);
}
```

### 사용 예제

```java
@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    // CrudRepository의 모든 메서드 + JPA 추가 기능
    
    // 커스텀 쿼리 메서드
    Optional<Member> findByEmail(String email);
    List<Member> findByIsActiveTrue();
    List<Member> findByRole(Member.MemberRole role);
    
    // 페이징 및 정렬을 위한 메서드
    Page<Member> findByDepartment(String department, Pageable pageable);
    List<Member> findByNameContaining(String name, Sort sort);
    
    // 네이티브 쿼리
    @Query("SELECT m FROM Member m WHERE m.joinDate >= :date")
    List<Member> findMembersJoinedAfter(@Param("date") LocalDateTime date);
    
    // 배치 삭제
    @Modifying
    @Query("DELETE FROM Member m WHERE m.isActive = false")
    int deleteInactiveMembers();
}

// 사용 예시
@Service
public class MemberJpaService {
    @Autowired
    private MemberJpaRepository memberRepository;
    
    public void jpaOperations() {
        // 기본 CRUD 작업 (CrudRepository 상속)
        Member member = new Member();
        member.setName("이영희");
        member.setEmail("lee@example.com");
        Member savedMember = memberRepository.save(member);
        
        // JPA 특화 기능
        
        // 1. 배치 작업
        List<Member> members = Arrays.asList(
            Member.builder().name("회원1").email("member1@test.com").build(),
            Member.builder().name("회원2").email("member2@test.com").build()
        );
        memberRepository.saveAllAndFlush(members);
        
        // 2. 페이징 조회
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<Member> memberPage = memberRepository.findAll(pageable);
        
        System.out.println("총 페이지 수: " + memberPage.getTotalPages());
        System.out.println("총 요소 수: " + memberPage.getTotalElements());
        System.out.println("현재 페이지 내용: " + memberPage.getContent());
        
        // 3. 정렬 조회
        Sort sort = Sort.by(Sort.Direction.DESC, "joinDate");
        List<Member> sortedMembers = memberRepository.findAll(sort);
        
        // 4. 참조 조회 (지연 로딩)
        Member reference = memberRepository.getReferenceById(1L);
        
        // 5. 배치 삭제
        memberRepository.deleteAllInBatch();
        
        // 6. 플러시 (영속성 컨텍스트 강제 동기화)
        memberRepository.flush();
    }
}
```

## 주요 차이점

### 1. 기능 범위

| 기능 | CrudRepository | JpaRepository |
|------|---------------|---------------|
| 기본 CRUD | ✅ | ✅ |
| 페이징 | ❌ | ✅ |
| 정렬 | ❌ | ✅ |
| 배치 작업 | ❌ | ✅ |
| 플러시 | ❌ | ✅ |

### 2. 반환 타입

```java
// CrudRepository
Iterable<T> findAll();           // Iterable 반환
Iterable<T> findAllById(...);

// JpaRepository
List<T> findAll();               // List 반환 (더 구체적)
List<T> findAllById(...);
```

### 3. 성능 최적화

```java
// CrudRepository - 개별 삭제
public void deleteMembers(List<Member> members) {
    for (Member member : members) {
        memberRepository.delete(member);  // N번의 DELETE 쿼리
    }
}

// JpaRepository - 배치 삭제
public void deleteMembersBatch(List<Member> members) {
    memberRepository.deleteAllInBatch(members);  // 1번의 DELETE 쿼리
}
```

### 4. 페이징과 정렬

```java
// CrudRepository에서는 불가능
// 페이징이나 정렬이 필요하면 PagingAndSortingRepository 사용해야 함

// JpaRepository에서는 가능
@Service
public class MemberPagingService {
    @Autowired
    private MemberJpaRepository memberRepository;
    
    public Page<Member> getMembersWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by("name").ascending().and(Sort.by("joinDate").descending()));
        return memberRepository.findAll(pageable);
    }
}
```

## 실제 프로젝트에서의 사용 예시

### 현재 프로젝트의 Repository 구조

```java
// src/main/java/com/handong/internationalmedia/repository/MemberRepository.java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByIsActiveTrue();
    List<Member> findByRole(Member.MemberRole role);
    List<Member> findByDepartment(String department);
}
```

### 서비스에서의 활용

```java
// src/main/java/com/handong/internationalmedia/service/MemberService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    // JpaRepository의 기본 메서드 활용
    public List<Member> getAllMembers() {
        return memberRepository.findAll();  // List<T> 반환
    }

    // 페이징 기능 활용
    public Page<Member> getMembersWithPaging(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    // 배치 작업 활용
    @Transactional
    public List<Member> createMembersInBatch(List<Member> members) {
        return memberRepository.saveAllAndFlush(members);
    }
}
```

## 선택 기준

### CrudRepository를 선택해야 하는 경우
- 간단한 CRUD 작업만 필요한 경우
- 페이징이나 정렬이 필요 없는 경우
- 최소한의 의존성을 원하는 경우
- 마이크로서비스에서 가벼운 구현이 필요한 경우

### JpaRepository를 선택해야 하는 경우 (권장)
- 페이징과 정렬이 필요한 경우
- 배치 작업이 필요한 경우
- 성능 최적화가 중요한 경우
- 풍부한 기능이 필요한 일반적인 웹 애플리케이션

## 결론

- **JpaRepository**는 **CrudRepository**를 확장한 인터페이스입니다.
- **CrudRepository**는 기본적인 CRUD 작업을 위한 최소한의 기능을 제공합니다.
- **JpaRepository**는 JPA 특화 기능, 페이징, 정렬, 배치 작업 등의 고급 기능을 추가로 제공합니다.
- 일반적인 웹 애플리케이션에서는 **JpaRepository**를 사용하는 것이 권장됩니다.
- 성능과 기능면에서 **JpaRepository**가 더 많은 장점을 제공합니다.

현재 프로젝트에서는 모든 Repository가 `JpaRepository`를 상속하고 있으며, 이는 향후 페이징, 정렬, 배치 작업 등의 요구사항에 대비한 좋은 선택입니다.