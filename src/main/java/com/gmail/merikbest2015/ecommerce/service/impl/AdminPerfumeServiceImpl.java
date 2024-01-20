package com.gmail.merikbest2015.ecommerce.service.impl;

import com.gmail.merikbest2015.ecommerce.constants.ErrorMessage;
import com.gmail.merikbest2015.ecommerce.constants.SuccessMessage;
import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.dto.request.PerfumeRequestPartOne;
import com.gmail.merikbest2015.ecommerce.dto.request.PerfumeRequestPartTwo;
import com.gmail.merikbest2015.ecommerce.dto.request.SearchRequest;
import com.gmail.merikbest2015.ecommerce.dto.response.MessageResponse;
import com.gmail.merikbest2015.ecommerce.repository.OrderRepository;
import com.gmail.merikbest2015.ecommerce.repository.PerfumeRepository;
import com.gmail.merikbest2015.ecommerce.repository.UserRepository;
import com.gmail.merikbest2015.ecommerce.service.AdminPerfumeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminPerfumeServiceImpl implements AdminPerfumeService {
    @Value("${upload.path}")
    private String uploadPath;
    private final PerfumeRepository perfumeRepository;
    private final ModelMapper modelMapper;


    @Override
    public Page<Perfume> getPerfumes(Pageable pageable) {
        return perfumeRepository.findAllByOrderByPriceAsc(pageable);
    }

    @Override
    public Page<Perfume> searchPerfumes(SearchRequest request, Pageable pageable) {
        return perfumeRepository.searchPerfumes(request.getSearchType(), request.getText(), pageable);
    }





    @Override
    public Perfume getPerfumeById(Long perfumeId) {
        return perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.PERFUME_NOT_FOUND));
    }

    @Override
    @SneakyThrows
    @Transactional
    public MessageResponse editPerfume(PerfumeRequestPartOne perfumeRequestPartOne, PerfumeRequestPartTwo perfumeRequestPartTwo, MultipartFile file) {
        return savePerfume(perfumeRequestPartOne,perfumeRequestPartTwo, file, SuccessMessage.PERFUME_EDITED);
    }

    @Override
    @SneakyThrows
    @Transactional
    public MessageResponse addPerfume(PerfumeRequestPartOne perfumeRequestPartOne, PerfumeRequestPartTwo perfumeRequestPartTwo, MultipartFile file) {
        return savePerfume(perfumeRequestPartOne,perfumeRequestPartTwo, file, SuccessMessage.PERFUME_ADDED);
    }



    private MessageResponse savePerfume(PerfumeRequestPartOne partOne, PerfumeRequestPartTwo partTwo, MultipartFile file, String message) throws IOException {
        Perfume perfume = new Perfume();

        modelMapper.map(partOne, perfume);

        modelMapper.map(partTwo, perfume);

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            perfume.setFilename(resultFilename);
        }

        perfumeRepository.save(perfume);
        return new MessageResponse("alert-success", message);
    }
}
