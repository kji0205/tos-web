package kr.go.togetherschool.tosweb.model;


public enum Role {

    //guest와 user라는 권한 목록 생성
    ROLE_GUEST("ROLE_GUEST", "GUEST"),
    ROLE_MEMBER("ROLE_MEMBER", "MEMBER"),
    ROLE_ADMIN("ROLE_ADMIN", "ADMIN");

    //각 권한이 가질 필드 선언 + 생성자 주입
    private final String key;
    private final String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public Role fromName(String type) {
        return Role.valueOf("ROLE_" + type.toUpperCase());
    }
}
