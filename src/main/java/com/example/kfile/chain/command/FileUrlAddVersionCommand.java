package com.example.kfile.chain.command;

import cn.hutool.core.date.DateUtil;
import com.example.kfile.chain.FileContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.stereotype.Service;

/**
 * 处理文件 url, 给直链增加版本号（原始链接不添加），防止浏览器缓存.
 */
@Service
public class FileUrlAddVersionCommand implements Command {

    /**
     * 处理文件 url, 给直链增加版本号，防止浏览器缓存.
     *
     * @param context 文件处理责任链上下文
     * @return 是否停止执行责任链, true: 停止执行责任链, false: 继续执行责任链
     */
    @Override
    public boolean execute(Context context) throws Exception {
        FileContext fileContext = (FileContext) context;
        //List<FileInfo> fileInfos = fileContext.getFileItemList();

        long version = DateUtil.currentSeconds();

        // fileItemList.forEach((item) -> {
        // 	// url 中不包含 ? 才添加此参数，否则可能会影响正常下载.
        // 	if (!StrUtil.contains(item.getUrl(), '?')) {
        // 		item.setUrl(item.getUrl() + "?v=" + version);
        // 	}
        //
        // });
        return false;
    }

}