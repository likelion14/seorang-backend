package com.likelion.seorang.service;

import com.likelion.seorang.dto.BoothInfoResDto;
import com.likelion.seorang.repository.BoothRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoothService {

    private final BoothRepository boothRepository;

    @Cacheable(value = "booths", key = "#day")
    public List<BoothInfoResDto> getBoothsByDay(Integer day) {
        if ( day < 1 || day > 3 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_DAY");
        }

        return boothRepository.findAllBy(day).stream()
                .map(booth -> BoothInfoResDto.from(booth, day))
                .toList();
    }
}
