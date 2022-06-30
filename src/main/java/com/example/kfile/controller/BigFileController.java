package com.example.kfile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/bigfile")
public class BigFileController {
/*
    private final String fileStorePath = "./tmp";

    *//**
     * Title: 判断文件是否上传过，是否存在分片，断点续传
     * Description:
     * 文件已存在，下标为-1
     * 文件没有上传过，下标为零
     * 文件上传中断过，返回当前已经上传到的下标
     *//*
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @ResponseBody
    public int checkBigFile(String fileMd5) {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9001")
                            .credentials("sDRDIb2VaWKBRIn817J3", "AJMnqvSgWXNlCGwFp5lmYS3TqE76tDQv9itW1BFG")
                            .build();
            boolean found =
                    minioClient.statObject(StatObjectArgs.builder().bucket("file").object(fileMd5).build()).etag() != null;
            if (found) {
                return -1;
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred: " + e);
        }
        File dir = new File("./tmp" + "/" + fileMd5);
        File[] childs = dir.listFiles();
        if (childs == null) {
            return 0;
        } else {
            return childs.length - 1;//文件上传中断过，返回当前已经上传到的下标
        }
    }

    *//**
     * 上传文件
     *//*
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void filewebUpload(MultipartFileParam param, HttpServletRequest request) {
        boolean isMultipart = true;
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            isMultipart = false;
        }
        // 文件名
        String fileName = param.getName();
        // 文件每次分片的下标
        int chunkIndex = param.getChunk();
        if (isMultipart) {
            File file = new File("./tmp" + "/" + param.getMd5());
            if (!file.exists()) {
                file.mkdir();
            }
            File chunkFile = new File(
                    "./tmp" + "/" + param.getMd5() + "/" + chunkIndex);
            try {
                FileUtils.copyInputStreamToFile(param.getFile().getInputStream(), chunkFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    *//**
     * 分片上传成功之后，合并文件
     *//*
    @RequestMapping(value = "/merge", method = RequestMethod.POST)
    @ResponseBody
    public String filewebMerge(HttpServletRequest request) {
        FileChannel outChannel = null;
        try {
            String fileName = request.getParameter("fileName");
            String fileMd5 = request.getParameter("fileMd5");
            // 读取目录里的所有文件
            File dir = new File("./tmp" + "/" + fileMd5);
            File[] childs = dir.listFiles();
            if (Objects.isNull(childs) || childs.length == 0) {
                return "找不到文件";
            }
            // 转成集合，便于排序
            List<File> fileList = new ArrayList<>(Arrays.asList(childs));
            fileList.sort((o1, o2) -> {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            });
            // 合并后的文件
            File outputFile = new File(fileStorePath + "/" + "merge" + "/" + fileMd5 + "/" + fileName);
            // 创建文件
            if (!outputFile.exists()) {
                File mergeMd5Dir = new File(fileStorePath + "/" + "merge" + "/" + fileMd5);
                if (!mergeMd5Dir.exists()) {
                    mergeMd5Dir.mkdirs();
                }
                outputFile.createNewFile();
            }
            outChannel = new FileOutputStream(outputFile).getChannel();
            FileChannel inChannel = null;
            try {
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inChannel.close();
                    // 删除分片
                    file.delete();
                }
                MinioClient minioClient =
                        MinioClient.builder()
                                .endpoint("http://127.0.0.1:9001")
                                .credentials("sDRDIb2VaWKBRIn817J3", "AJMnqvSgWXNlCGwFp5lmYS3TqE76tDQv9itW1BFG")
                                .build();
                // 创建一个 InputStream 流来读取本地文件
                InputStream inputStream = new FileInputStream(outputFile);
                // 使用 putObject() 方法将文件上传到 MinIO 存储桶中
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket("file")                 // 存储桶名称
                        .object(fileMd5)                 // 对象名称
                        .stream(
                                inputStream,  // 文件内容字节流
                                outputFile.length(),                      // 文件大小
                                -1                                // 选项，告诉 SDK 读取整个流
                        )
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
                //发生异常，文件合并失败 ，删除创建的文件
                outputFile.delete();
                dir.delete();//删除文件夹
            } finally {
                if (inChannel != null) {
                    inChannel.close();
                }
            }
            dir.delete(); //删除分片所在的文件夹
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "合并成功";
    }*/
}