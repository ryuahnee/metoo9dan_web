package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.EducationRepository;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.education.vaildation.EducationVaildation;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EducationalService {

    private final EducationRepository educationalRepository;
    private final ResourcesFilesReprository resourcesFilesReprository;

    //학습자료등록 (파일같이저장)
    public void save(EducationVaildation educationVaildation) throws IOException {
        EducationalResources educationalResources = new EducationalResources();
        ResourcesFiles resourcesFiles = new ResourcesFiles();

        //파일을 첨부하면 'yyyyMMddHHmmss+원본파일명'으로 저장된다.
        if(educationVaildation.getBoardFile() != null){

            //교육자료 내용 저장
            educationalResources.setResourceName(educationVaildation.getResource_name());
            educationalResources.setResourceCate(educationVaildation.getResource_cate());
            educationalResources.setFileType(educationVaildation.getFile_type());
            educationalResources.setServiceType(educationVaildation.getService_type());
            educationalResources.setDescription(educationVaildation.getDescription());
            educationalResources.setCreationDate(LocalDateTime.now());
            educationalResources.setGameContents(null);

            //교육자료파일 저장
            resourcesFiles.setEducationalResources(educationalResources);
            MultipartFile boardfile = educationVaildation.getBoardFile();
            String originalFileName = boardfile.getOriginalFilename(); //파일이름을 가져옴
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String copyFileName = todayDate + "_" + originalFileName;    //파일저장명 'yyyyMMddHHmmss+원본파일명'
            String savePath = "/Users/ryuahn/Desktop/baduk/education/"+copyFileName; //mac 파일 지정 C:/baduk
            boardfile.transferTo(new File(savePath));   //파일저장 처리

            resourcesFiles.setOriginFileName(originalFileName);
            resourcesFiles.setCopyFileName(copyFileName);

            educationalResources.setFileUrl(savePath);
            educationalRepository.save(educationalResources);
            resourcesFilesReprository.save(resourcesFiles);
        }else {
            //교육자료 내용 저장
            educationalResources.setResourceName(educationVaildation.getResource_name());
            educationalResources.setResourceCate(educationVaildation.getResource_cate());
            educationalResources.setFileType(educationVaildation.getFile_type());
            educationalResources.setFileUrl(null);
            educationalResources.setServiceType(educationVaildation.getService_type());
            educationalResources.setDescription(educationVaildation.getDescription());
            educationalResources.setCreationDate(LocalDateTime.now());

            //교육자료파일 저장
            educationalRepository.save(educationalResources);
        }
    }

}
