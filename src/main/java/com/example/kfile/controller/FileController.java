package com.example.kfile.controller;import com.example.kfile.domain.request.FileListRequest;import com.example.kfile.domain.result.FileEntry;import com.example.kfile.domain.result.FileEntryList;import com.example.kfile.service.FileService;import com.example.kfile.util.AjaxJson;import com.example.kfile.util.FileSortUtil;import io.swagger.annotations.Api;import io.swagger.annotations.ApiOperation;import jakarta.validation.Valid;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.web.bind.annotation.PostMapping;import org.springframework.web.bind.annotation.RequestBody;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;import java.io.FileNotFoundException;import java.util.List;/** * 文件列表相关接口 */@Api(tags = "文件列表模块")@Slf4j@RequestMapping("/api/fileItem")@RestControllerpublic class FileController {    private FileService fileService;    @Autowired    public void setFileService(FileService fileService) {        this.fileService = fileService;    }    @ApiOperation(value = "获取文件列表")    @PostMapping("/files")    public AjaxJson<FileEntryList> list(@Valid @RequestBody FileListRequest fileListRequest) throws Exception {        // 处理请求参数默认值        fileListRequest.handleDefaultValue();        List<FileEntry> fileEntries = fileService.fileList(fileListRequest.getDirectory());        FileSortUtil.sort(fileEntries, fileListRequest);        return AjaxJson.getSuccessData(new FileEntryList(fileEntries));    }    @ApiOperation(value = "获取单个文件信息")    @PostMapping("/file/item")    public AjaxJson<?> fileItem(@Valid @RequestBody String fileItemId) throws FileNotFoundException {        FileEntry fileEntry = fileService.getFileEntry(fileItemId);        return AjaxJson.getSuccessData(fileEntry);    }}