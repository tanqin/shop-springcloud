package com.shop.controller;

import com.shop.entity.Result;
import com.shop.entity.StatusCode;
import com.shop.file.FastDFSFile;
import com.shop.util.FastDFSUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class FileController {

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam(value = "file") MultipartFile file) throws Exception {
        // 封装文件信息
        FastDFSFile fastDFSFile = new FastDFSFile(file.getOriginalFilename(), file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()));

        // 调用 FastDFSUtil 工具类将文件上传到 FastDFS 中
        String[] uploads = FastDFSUtil.upload(fastDFSFile);

        // 拼接文件访问地址
        String url = FastDFSUtil.getTrackerInfo() + uploads[0] + "/" + uploads[1];

        return new Result(true, StatusCode.OK, "上传成功！", url);
    }
}
