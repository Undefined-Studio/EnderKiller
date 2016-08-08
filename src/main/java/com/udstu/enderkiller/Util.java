package com.udstu.enderkiller;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by czp on 16-8-7.
 * Util class
 */
public class Util {
    //将准备输出的内容分页并返回对应页内容
    private static String[] transToPaginatedMessages(String title, String header, List<String> messageList, int page) {
        messageList.add(0, header);
        int row = 9;    //聊天栏未展开时能显示10行,实际内容9行
        int totalMessage = messageList.size();
        int totalPage = (totalMessage - (totalMessage % row)) / row + 1;
        List<String> messageInPage = new ArrayList<>();
        int start;
        int end;

        //页码越界时取对应边界值
        page = page < 1 ? 1 : (page > totalPage ? totalPage : page);

        messageInPage.add("---------- " + title + " (" + page + "/" + totalPage + ") --------------------");
        try {
            start = (page - 1) * row;
            end = start + row;
            for (int i = start; i < end; i++) {
                messageInPage.add(messageList.get(i));  //不足一页时将越界,提前结束循环
            }
        } catch (Exception e) {

        }

        return messageInPage.toArray(new String[messageInPage.size()]);
    }

    //对内容进行筛选
    private static List<String> selectMessages(List<String> messageList, String[] searchArgs) {
        List<String> selectedMessageList = new ArrayList<>();
        Pattern pattern;
        String patternStr = "^.*";
        Matcher matcher;

        for (String arg : searchArgs) {
            patternStr += arg + ".*";
        }
        pattern = Pattern.compile(patternStr);

        for (String message : messageList) {
            matcher = pattern.matcher(message);
            if (matcher.matches()) {
                selectedMessageList.add(message);
            }
        }

        return selectedMessageList;
    }

    //判断是否是整数
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //发送信息(s)给对象
    public static void sendMessages(CommandSender commandSender, String title, String header, List<String> messageList, String[] endArgs) {
        int page;
        String[] searchArgs;
        String searchArg = "";

        if (endArgs.length == 0) {
            page = 1;
        } else {
            //若最后一个是数字,则设为page,否则page为1
            if (Util.isInteger(endArgs[endArgs.length - 1])) {
                page = Integer.parseInt(endArgs[endArgs.length - 1]);
                searchArgs = Arrays.copyOfRange(endArgs, 0, endArgs.length - 1);
            } else {
                page = 1;
                searchArgs = Arrays.copyOfRange(endArgs, 0, endArgs.length);
            }

            //若搜索条件不为空,则进行搜索
            if (searchArgs.length != 0) {
                messageList = selectMessages(messageList, searchArgs);

                //将搜索参数集中到一个字符串中
                for (String arg : searchArgs) {
                    searchArg += arg + " ";
                }

                //若搜索结果为空
                if (messageList.size() == 0) {
                    commandSender.sendMessage(R.getLang("noResultOf").replace("{$arg}", searchArg));
                    return;
                }

                title += ": " + R.getLang("search");
                header = R.getLang("searchFor").replace("{$arg}", ": " + searchArg);
            } else {
                title += ": " + R.getLang("index");
            }
        }

        commandSender.sendMessage(transToPaginatedMessages(title, header, messageList, page));
    }
}
