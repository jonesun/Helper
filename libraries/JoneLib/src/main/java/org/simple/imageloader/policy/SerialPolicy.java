/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 bboyfeiyu@gmail.com ( Mr.Simple )
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.simple.imageloader.policy;

import org.simple.imageloader.request.BitmapRequest;

/**
 * 顺序加载策略
 * 
 * @author mrsimple
 */
public class SerialPolicy implements LoadPolicy {

    @Override
    public int compare(BitmapRequest request1, BitmapRequest request2) {
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return request1.serialNum - request2.serialNum;
    }

}
