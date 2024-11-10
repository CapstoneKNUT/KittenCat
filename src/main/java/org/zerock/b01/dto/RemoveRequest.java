package org.zerock.b01.dto;

public class RemoveRequest {

    private String username;
    private Long sno;

    // 기본 생성자
    public RemoveRequest() {}

    // Getter 메소드
    public String getUsername() {
        return username;
    }

    public Long getSno() {
        return sno;
    }

    // Setter 메소드
    public void setUsername(String username) {
        this.username = username;
    }

    public void setSno(Long sno) {
        this.sno = sno;
    }
}
