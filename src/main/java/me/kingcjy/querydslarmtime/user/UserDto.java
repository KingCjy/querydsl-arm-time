package me.kingcjy.querydslarmtime.user;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class UserDto {
    private final Long userId;
    private final String name;
    private final ZonedDateTime createdAt;
}
