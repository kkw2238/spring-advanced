package org.example.expert.domain.user.dto.response;

import lombok.Getter;

/** 수정된 코드 : record Class로 변환
 * 불변성 데이터를 저장하는 용도로 사용하므로 record Class로 변경
 * @param id 유저 id
 * @param email 유저 email
 */
public record UserResponse(Long id, String email) {

}
