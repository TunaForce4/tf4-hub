package com.tunaforce.hub.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HubException {

    // Hub
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "허브 정보를 찾을 수 없습니다."),
    HUB_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 동일한 허브명이 존재합니다."),
    INVALID_HUB_ADDRESS(HttpStatus.BAD_REQUEST, "유효하지 않은 주소입니다."),
    HUB_NO_CHANGE(HttpStatus.BAD_REQUEST, "변경 사항이 없습니다."),
    INVALID_PAGE_OR_SIZE(HttpStatus.BAD_REQUEST, "page, size는 0 이상의 정수여야 합니다."),

    // HubRoute
    INVALID_HUB_ROUTE(HttpStatus.BAD_REQUEST, "유효한 경로를 찾을 수 없습니다."),
    HUB_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "허브 간 이동 경로를 찾을 수 없습니다."),

    // Auth
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),
    AUTH_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스가 동작하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}