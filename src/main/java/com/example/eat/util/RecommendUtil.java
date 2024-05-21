package com.example.eat.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RecommendUtil {
    public List<String> getMusicRecommend(Integer id) {
        List<String> snList = null;
        try {
            //Python解释器
            String s1 = "/usr/local/product/Anaconda/anaconda3/envs/disk_detect_env/bin/python";
            String[] args1 = new String[]{s1, "/usr/local/product/Python/Health/recommendationMusic.py", String.valueOf(id)};
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            String ids = null;
            while ((line = in.readLine()) != null) {
                ids = line;
            }
            if (ids != null) {
                snList = Arrays.asList(ids.split(","));
            }else {
                return null;
            }

            in.close();
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return snList;
    }
    public List<String> getMusicListRecommend(Integer id) {
        List<String> snList = null;
        try {
            //Python解释器
            String s1 = "/usr/local/product/Anaconda/anaconda3/envs/disk_detect_env/bin/python";
            String[] args1 = new String[]{s1, "/usr/local/product/Python/Health/recommendationMusicList.py", String.valueOf(id)};
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            String ids = null;
            while ((line = in.readLine()) != null) {
                ids = line;
            }
            if (ids != null) {
                snList = Arrays.asList(ids.split(","));
            }else {
                return null;
            }

            in.close();
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return snList;
    }
    public List<String> getCoobookRecommend(Integer id) {
        List<String> snList = null;
        try {
            //Python解释器
            String s1 = "/usr/local/product/Anaconda/anaconda3/envs/disk_detect_env/bin/python";
            String[] args1 = new String[]{s1, "/usr/local/product/Python/Health/recommendationCookbook.py", String.valueOf(id)};
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            String ids = null;
            while ((line = in.readLine()) != null) {
                ids = line;
            }
            if (ids != null) {
                snList = Arrays.asList(ids.split(","));
            }else {
                return null;
            }

            in.close();
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return snList;
    }
}
