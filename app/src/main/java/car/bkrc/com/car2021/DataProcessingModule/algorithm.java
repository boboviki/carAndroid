package car.bkrc.com.car2021.DataProcessingModule;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

public class algorithm {



//	public static void main(String[] args)
//	{
//		CRC_Code("(<AaBbCcDd>/<g(x)=x16+x15+x2+1>)",CRC_order_data);
//
//		Affine("A/5,2;B\\C[D.y(EF]Gf,3,7",Affine_order_data);
//
//		RSA_Code("(<3,4,5,7,8,9,19>/<1,2,3,4,5,6>)",RSA_order_data);
//	}

        /****************************************************************************************************
         RSA
         ****************************************************************************************************/

        //RSA解密
        public  void RSA_Code(String SrcString,byte[] order_buffer)
        {
            char s1=0,s2=0,s3=0;
            int  temp=0;
            char Num1[] = new char[6];		//密文
            char Num2[] = new char[7];		//参数区
            char Num3[] = new char[2];		//存储p,q
            char Qr_SrcString[] = new char[SrcString.length()];

            int i=0;
            int j=0;
            int k=0;
            int p=0,q=0,n=0,r=0,e=0,d=0;
            boolean Flag = false;


            for(i = 0; i < SrcString.length(); i++ )				 //将字符串存放在数组中
            {
                Qr_SrcString[i] = SrcString.charAt(i);
            }


            for (i = 0; i < Qr_SrcString.length; i++)				//提取明文位置
            {
                s1 = Qr_SrcString[i];
                s2 = Qr_SrcString[i+1];
                if((s1== '/') && (s2== '<'))
                {
                    temp = i;
                    break;
                }
            }

            for (i = (temp+1); i < (Qr_SrcString.length-2); i++)					//提取明文
            {
                s1 = Qr_SrcString[i];
                s2 = Qr_SrcString[i+1];
                s3 = Qr_SrcString[i+2];
                if(j<6)
                {
                    if((s1== ',')&&(s2>= '0' && s2<= '9')&&(s3>= '0' && s3<= '9'))
                    {
                        Num1[j] =(char) ((s2-'0')*10+s3-'0');
                        j++;
                    }
                    else if((s1== ',')&&(s2>= '0' && s2<= '9')&&(s3== ','))
                    {
                        Num1[j] = (char) (s2-'0');
                        j++;
                    }
                    else if((s1== ',')&&(s2>= '0' && s2<= '9')&&(s3== '>'))
                    {
                        Num1[j] = (char) (s2-'0');
                        j++;
                    }
                    else if((s1== '<')&&(s2>= '0' && s2<= '9')&&(s3== ','))
                    {
                        Num1[j] = (char) (s2-'0');
                        j++;
                    }
                    else if((s1== '<')&&(s2>= '0' && s2<= '9')&&(s3>= '0' && s3<= '9'))
                    {
                        Num1[j] =(char) ((s2-'0')*10+s3-'0');
                        j++;
                    }
                }
            }


            j = 0;

            for (i = 1; i < (Qr_SrcString.length-2); i++)										//提取参数
            {
                s1 = Qr_SrcString[i];
                s2 = Qr_SrcString[i+1];
                s3 = Qr_SrcString[i+2];
                if(j<7)
                {
                    if((s1== ',')&&(s2>= '0' && s2<= '9')&&(s3>= '0' && s3<= '9'))
                    {
                        Num2[j] =(char) ((s2-'0')*10+s3-'0');
                        j++;
                    }
                    else if((s1== ',')&&(s2>= '0' && s2<= '9')&&(s3== ','))
                    {
                        Num2[j] = (char) (s2-'0');
                        j++;
                    }
                    else if((s1== ',')&&(s2>= '0' && s2<= '9')&&(s3== '>'))
                    {
                        Num2[j] = (char) (s2-'0');
                        j++;
                    }
                    else if((s1== '<')&&(s2>= '0' && s2<= '9')&&(s3== ','))
                    {
                        Num2[j] = (char) (s2-'0');
                        j++;
                    }
                    else if((s1== '<')&&(s2>= '0' && s2<= '9')&&(s3>= '0' && s3<= '9'))
                    {
                        Num2[j] =(char) ((s2-'0')*10+s3-'0');
                        j++;
                    }
                }
            }

            BubbleSort(Num2, 7);

            i=0;

            //找出最大且不相等的两个质数
            for(i=7;i>0;i--)
            {
                if(isprime(Num2[i-1])==true)
                {
                    if(k<2)
                    {
                        Num3[k] = Num2[i-1];
                        k++;
                    }
                }
            }


            p = Num3[0];
            q = Num3[1];

            n = p * q;
            r = (p-1) * (q-1);

            for(e=2;e<r;e++)																		//得到 d
            {
                if((r%e)!=0)
                {
                    for(d=1;d<65536;d++)
                    {
                        if(((e*d)%r)==1)
                        {
                            Flag = true;
                            break;
                        }
                    }
                }
                if(Flag) break;
            }

            order_buffer[0] = (byte) (candp(Num1[0],d,n)%255);
            order_buffer[1] = (byte) (candp(Num1[1],d,n)%255);
            order_buffer[2] = (byte) (candp(Num1[2],d,n)%255);
            order_buffer[3] = (byte) (candp(Num1[3],d,n)%255);
            order_buffer[4] = (byte) (candp(Num1[4],d,n)%255);
            order_buffer[5] = (byte) (candp(Num1[5],d,n)%255);


            for(i = 0 ; i < 6; i++)
            {
                System.out.println(BToH((char)(order_buffer[i])));
            }

        }


        // a = 待加密数字或解密数字
        // b = e:加密 d:解密
        // c = n
        //返回值 = 加密结果
        static int candp(int a,int b,int c)
        {
            int r=1;
            b=b+1;
            while(b!=1)
            {
                r=r*a;
                r=r%c;
                b--;
            }
            return r;
        }

        static void BubbleSort (char[] pData,int Count)
        {
            char iTemp;
            for(int i=1; i<Count; i++)
            {
                for (int j=Count-1; j>=1; j--)
                {
                    if (pData[j]<pData[j-1])
                    {
                        iTemp=pData[j-1];
                        pData[j-1]=pData[j];
                        pData[j]=iTemp;
                    }
                }
            }
        }



        //判断一个数是否是质数
        boolean isprime(char a)
        {
            for(int i=2;i<=Math.sqrt((double)a);i++)
                if(a%i==0) return false;
            return true;
        }



        /****************************************************************************************************
         CRC
         ****************************************************************************************************/

        //CRC     (<AaBbCcDd>/<g(x)=x16+x15+x2+1>)

        private   byte[] data = {0x41,0x61,0x42,0x62};

        public  void CRC_Code(String SrcString,byte[] order_buffer)
        {
            char s = 0;
            char s1=0,s2=0,s3=0;
            char temp = 0;
            char[] buf = new char[4];
            char[] Num = new char[3];
            int i=0;
            int j=0;
            char PolyCode = 0;

            char  CRC = 0xFFFF;   //CRC寄存器
            char  CRC2 = 0;

            //SrcString为二维码里获取的字符串
            //获取前两个明文字符
            for (i = 0; i < SrcString.length(); i++)
            {
                s = SrcString.charAt(i);
                if((s>= 'a' && s<= 'z'&&s!='x') || (s>='A' && s<= 'Z'&&s!='X'))
                {
                    buf[temp] = s;
                    temp++;
                    if(temp>=2) break;
                }
            }

            //获取后两个明文字符
            temp = 0;
            for (i = (SrcString.length()-1); i>=0; i--)
            {
                s = SrcString.charAt(i);
                if((s>= 'a' && s<= 'z'&&s!='x') || (s>='A' && s<= 'Z'&&s!='X'))
                {
                    buf[3-temp] = s;
                    temp++;
                    if(temp>=2) break;
                }
            }

            //提取多项式码
            for (i = 0; i < SrcString.length(); i++)
            {
                s1 = SrcString.charAt(i);
                if(s1=='x')
                {
                    //s1 = SrcString.GetAt(i);
                    s2 = SrcString.charAt(i+1);
                    s3 = SrcString.charAt(i+2);
                    if(j<3)
                    {
                        if((s1=='x')&&(s2>= '0' && s2<= '9')&&(s3<'0'||s3>'9'))
                        {
                            Num[j] = (char)(s2-'0');
                            j++;
                        }
                        else if((s1=='x')&&(s2>= '0' && s2<= '9')&&(s3>= '0' && s3<= '9'))
                        {
                            Num[j] = (char)((s2-'0')*10+s3-'0');
                            if(Num[j]>9&&Num[j]<16)
                            {
                                j++;
                            }
                        }
                    }
                }
            }
            PolyCode = (char)( 0x0001+(0x0001<<(Num[0]))+(0x0001<<(Num[1]))+(0x0001<<(Num[2])));
            Log.e("data","------>");
            Log.e("data",BToH(PolyCode));
            Log.e("data","----->>");
            Log.e("data",BToH(WORD_WordInvert(PolyCode)));

            for(j=0;j<4;j++)
            {
                CRC =  (char) (CRC ^ buf[j]);
                for(i= 0;i<8;i++)
                {
                    CRC2 = (char) (CRC & 0x0001);
                    if(CRC2 == 0x0001)
                    {
                        CRC = (char)((CRC>>1)^ WORD_WordInvert(PolyCode));
                    }
                    else
                    {
                        CRC = (char)(CRC>>1);
                    }
                }
            }

            order_buffer[0] = (byte)( (CRC>>8)&0xFF);  	//得到高位

            order_buffer[5] = (byte) (CRC&0xFF);	//得到低位

            System.out.println(BToH(CRC));
            Log.e("data",BToH(CRC));

            for (i = 0; i < 4; i++)
            {
                order_buffer[i+1] = (byte) buf[i];
            }
            Log.e("data","----->>");
            for(i = 0;i < 6; i++)
            {
                Log.e("data",""+BToH((char)((order_buffer[i])&0xFF)));
            }
        }


        private static char ByteInvert(char temp)
        {
            char[] sta ={0x00,0x08,0x04,0x0C,0x02,0x0A,0x06,0x0E,0x01,0x09,0x05,0x0D,0x03,0x0B,0x07,0x0F};
            char d = 0;
            d |= (sta[temp&0xF]) << 4;
            d |= sta[(temp>>4)&0xF];
            return d;
        }

        private static char WORD_WordInvert(char w)
        {
            char temp = 0;
            char d = 0;

            temp=(char)(w&0xFF);
            temp=ByteInvert(temp);
            d=(char) ((temp<<8)&0xFF00);
            temp=(char)((w>>8)&0xFF);
            temp=ByteInvert(temp);
            d|=temp;
            return d;

/*		byte temp = 0;
		char d = 0;

		temp=(byte)(w&0xFF);
		temp=ByteInvert(temp);
		d=(char) ((temp<<8)&0xFF00);
		temp=(byte)((w>>8)&0xFF);
		temp=ByteInvert(temp);
		d|=temp;
		return d;*/
        }


        /****************************************************************************************************
         仿射密码解密
         ****************************************************************************************************/


        public  void Affine(String SrcString,byte[] order_buffer)
        {
            char s1=0,s2=0;
            char Num1[]= new char[7];//待解密密文
            char Num2[]= new char[7];//解密后明文
            char[] Qr_SrcString = new char[SrcString.length()];
            int i=0;
            int j=0;
            int K1=0,K2=0,K3=0,b=0;;

            char t1[] = { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' };
            char t2[] = { 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };


            for(i = 0; i < SrcString.length(); i++ )				 //将字符串存放在数组中
            {
                Qr_SrcString[i] = SrcString.charAt(i);
            }



            for (i = 0; i < Qr_SrcString.length; i++)				//提取出字符串中前7个大写字母
            {
                s1 = Qr_SrcString[i];
                if((s1>= 'A') && (s1<= 'Z'))
                {
                    if(j<7)
                    {
                        Num1[j] = s1;
                        j++;
                    }
                }
            }



            for (i = 0; i < Qr_SrcString.length; i++)
            {
                s1 = Qr_SrcString[i];
                if((s1== '0') | (s1== '3')| (s1== '5')| (s1== '7')| (s1== '9'))
                {
                    K1 = s1-'0';
                    break;
                }
            }

            for (i = Qr_SrcString.length; i > 0; i--)
            {
                s2 = Qr_SrcString[i-1];
                if((s2>= '0') && (s2<= '9'))
                {
                    K2 = s2-'0';
                    break;
                }
            }

            while(((K1*K3)%26)!=1)
            {
                if(K3>65535) {return;}
                K3++;
            }


            for (int n = 0; n < 7; n++)
            {
                for (int l = 0; l < 26; l++)
                {
                    if (Num1[n] == t2[l])
                    {
                        if(l<K2)
                            b = 26-((K3 *(K2 - l)) % 26);
                        else
                            b = (K3 *(l - K2)) % 26;

                        Num2[n] = t1[b];
                    }
                }
            }



            for(i = 0;i<6;i++)
            {
                if((i%2)==0)
                {
                    order_buffer[i] = (byte) ((Math.abs(Num2[i+1]-Num2[i]))%255);

                }
                else
                {
                    order_buffer[i] = (byte) ((Math.abs(Num2[i+1]+Num2[i]))%255);
                }
            }

            for(i =0; i<6; i++)
            {
                System.out.println(BToH((char)(order_buffer[i]&0xFF)));

            }


        }

    /**********************************************************************************
     * 							QR识别结果处理
     * *******************************************************************************/
    //字符串通过处理转化为字符串的数组
    public static String[] S2Arr(String result){
        //byte[] resultByt;
        String[] resultArr;

        result = result.substring(1, result.length() - 1);//数据处理，掐头去尾
        result = result.replace("0x","");//数据处理，去除0x
        //qr_result=qr_result.replace(",","");//数据处理，去除，
        // qr_result=qr_result.replace(",","");
        //resultByt=result.getBytes();//字符串转字节
        resultArr = result.split(",");//字符串转数组,通过，号分割

        return  resultArr;
        }
        /**********************************************************************************
         * 							数据处理
         * *******************************************************************************/

        // 字符串数组转化为字节数组
     public static short[] Arr2Sho(String[] result){
            short[]  Sho=null;
            int[] num=new int[result.length];
            try {
                for (int i=0;i<result.length;i++)
                {
                    num[i] = algorithm.OxStringtoInt(result[i]);//将16进制字符串转为10进制的int
                    // qr_resultSho[i]=Short.valueOf(algorithm.OxStringtoInt(qr_resultArr[i])+"");//int转为short
                }
                Sho=new short[num.length];
                Sho=algorithm.shortint2hex(num);
               // Log.d("auto", "shortnum1 = " + Short.valueOf(algorithm.OxStringtoInt(resultArr[0])+""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.d("qr", "二维码识别结果"+result);
            return Sho;
        }

        // 二进制转十六进制
        public String BToH(char a)
        {
            String b = Integer.toHexString(a);
            return b;
        }

        //将字符串转十进制
        public static int SToB(String a)
        {
            int b = Integer.parseInt(a);

            System.out.println("STOB"+b);
            return b;
        }

    //将16进制字符串转为10进制的int（这种方法不需要处理ff开头的）
    public static int OxStringtoInt(String ox) throws Exception {
        ox=ox.toLowerCase();
        if(ox.startsWith("0x")){
            ox=ox.substring(2, ox.length() );
        }
        int ri = 0;
        int oxlen = ox.length();
        if (oxlen > 8)
            throw (new Exception("too lang"));
        for (int i = 0; i < oxlen; i++) {
            char c = ox.charAt(i);
            int h;
            if (('0' <= c && c <= '9')) {
                h = c - 48;
            } else if (('a' <= c && c <= 'f'))
            {
                h = c - 87;

            }
            else if ('A' <= c && c <= 'F') {
                h = c - 55;
            } else {
                throw (new Exception("not a integer "));
            }
            byte left = (byte) ((oxlen - i - 1) * 4);
            ri |= (h << left);
        }
        return ri;

    }


    //字符串截取
        public String cut_out(String data,int start,int end)
        {
            String Temp_data = null;
            Temp_data =data.substring(start, end);
            return Temp_data;
        }


    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    //十六进制转字符串
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    //转化字符串为十六进制编码
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


    // 转化十六进制编码为字符串
    public static String toStringHex1(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    // 转化十六进制编码为字符串
    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /*
     * 16进制数字字符集
     */
    private static String hexString = "0123456789ABCDEF";
    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }
    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    // 将指定byte数组以16进制的形式打印到控制台

        /**
         * 将指定byte数组以16进制的形式打印到控制台
         * @param hint String
         * @param b byte[]
         * @return void
         */
        public void printHexString(String hint, byte[] b) {
            System.out.print(hint);
            for (int i = 0; i < b.length; i++) {
                String hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                System.out.print(hex.toUpperCase() + " ");
            }
            System.out.println("");
        }
        /**
         * @param b byte[]
         * @return String
         */
        public static String Bytes2HexString(byte[] b) {
            String ret = "";
            for (int i = 0; i < b.length; i++) {
                String hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                ret += hex.toUpperCase();
            }
            return ret;
        }
        /**
         * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
         * @param src0 byte
         * @param src1 byte
         * @return byte
         */
        public static byte uniteBytes(byte src0, byte src1) {
            byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
                    .byteValue();
            _b0 = (byte) (_b0 << 4);
            byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
                    .byteValue();
            byte ret = (byte) (_b0 ^ _b1);
            return ret;
        }
        /**
         * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
         * @param src String
         * @return byte[]
         */
        public static byte[] HexString2Bytes(String src,int j) {
            byte[] ret = new byte[j];
            byte[] tmp = src.getBytes();
            for (int i = 0; i < i; i++) {
                ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
            }
            return ret;
        }

      /*********
       * 获取字符串的编码、修改字符串编码
       * *****************/
      // 获取字符串的编码
      public static String getEncoding(String str) {
          String encode = "GB2312";
          try {
              if (str.equals(new String(str.getBytes(encode), encode))) {
                  String s = encode;
                  return s;
              }
          } catch (Exception exception) {
          }
          encode = "ISO-8859-1";
          try {
              if (str.equals(new String(str.getBytes(encode), encode))) {
                  String s1 = encode;
                  return s1;
              }
          } catch (Exception exception1) {
          }
          encode = "UTF-8";
          try {
              if (str.equals(new String(str.getBytes(encode), encode))) {
                  String s2 = encode;
                  return s2;
              }
          } catch (Exception exception2) {
          }
          encode = "GBK";
          try {
              if (str.equals(new String(str.getBytes(encode), encode))) {
                  String s3 = encode;
                  return s3;
              }
          } catch (Exception exception3) {
          }
          return "";
      }
        //2.将字符串编码转为指定编码，如转为gbk
        //    public static String changEncod(String str) throws UnsupportedEncodingException {
        //        //String fileDir = "d:/str.txt";
        //        //String str = txt2String(fileDir);
        //        //获取原编码
        //        String encod = getEncoding(str);
        //        //转为gbk
        //      String s = new String(str.getBytes(encod),"gbk");
        //
        //    }
        //3.获取字符串内容
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String txt2String(String file) {
        //File file = new File(filePath);
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    //改字符串编码
    public static String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            // 用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            // 用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }


    /**
     * byte数组转short数组
     * @param data
     * @return
     */
    public static short[] byteToShort(byte[] data) {
        short[] shortValue = new short[data.length / 2];
        for (int i = 0; i < shortValue.length; i++) {
            shortValue[i] = (short) ((data[i * 2] & 0xff) | ((data[i * 2 + 1] & 0xff) << 8));
        }
        return shortValue;
    }
    /**
     * short数组转byte数组
     * @param data
     * @return
     */
    public static byte[] shortToByte(short[] data) {
        byte[] byteValue = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byteValue[i * 2] = (byte) (data[i] & 0xff);
            byteValue[i * 2 + 1] = (byte) ((data[i] & 0xff00) >> 8);
        }
        return byteValue;
    }


    public static short[] shortint2hex(int[] data)
    {
        short[] shortValue = new short[data.length];
        for(int i=0;i < data.length; i++){
            switch (data[i])
            {
                case 0:shortValue[i]= 0x00;break;
                case 1:shortValue[i]= 0x01;break;
                case 2:shortValue[i]= 0x02;break;
                case 3:shortValue[i]= 0x03;break;
                case 4:shortValue[i]= 0x04;break;
                case 5:shortValue[i]= 0x05;break;
                case 6:shortValue[i]= 0x06;break;
                case 7:shortValue[i]= 0x07;break;
                case 8:shortValue[i]= 0x08;break;
                case 9:shortValue[i]= 0x09;break;
                case 10:shortValue[i]= 0x0A;break;
                case 11:shortValue[i]= 0x0B;break;
                case 12:shortValue[i]= 0x0C;break;
                case 13:shortValue[i]= 0x0D;break;
                case 14:shortValue[i]= 0x0E;break;
                case 15:shortValue[i]= 0x0F;break;
                case 16:shortValue[i]= 0x10;break;
                case 17:shortValue[i]= 0x11;break;
                case 18:shortValue[i]= 0x12;break;
                case 19:shortValue[i]= 0x13;break;
                case 20:shortValue[i]= 0x14;break;
                case 21:shortValue[i]= 0x15;break;
                case 22:shortValue[i]= 0x16;break;
                case 23:shortValue[i]= 0x17;break;
                case 24:shortValue[i]= 0x18;break;
                case 25:shortValue[i]= 0x19;break;
                case 26:shortValue[i]= 0x1A;break;
                case 27:shortValue[i]= 0x1B;break;
                case 28:shortValue[i]= 0x1C;break;
                case 29:shortValue[i]= 0x1D;break;
                case 30:shortValue[i]= 0x1E;break;
                case 31:shortValue[i]= 0x1F;break;
                case 32:shortValue[i]= 0x20;break;
                case 33:shortValue[i]= 0x21;break;
                case 34:shortValue[i]= 0x22;break;
                case 35:shortValue[i]= 0x23;break;
                case 36:shortValue[i]= 0x24;break;
                case 37:shortValue[i]= 0x25;break;
                case 38:shortValue[i]= 0x26;break;
                case 39:shortValue[i]= 0x27;break;
                case 40:shortValue[i]= 0x28;break;
                case 41:shortValue[i]= 0x29;break;
                case 42:shortValue[i]= 0x2A;break;
                case 43:shortValue[i]= 0x2B;break;
                case 44:shortValue[i]= 0x2C;break;
                case 45:shortValue[i]= 0x2D;break;
                case 46:shortValue[i]= 0x2E;break;
                case 47:shortValue[i]= 0x2F;break;
                case 48:shortValue[i]= 0x30;break;
                case 49:shortValue[i]= 0x31;break;
                case 50:shortValue[i]= 0x32;break;
                case 51:shortValue[i]= 0x33;break;
                case 52:shortValue[i]= 0x34;break;
                case 53:shortValue[i]= 0x35;break;
                case 54:shortValue[i]= 0x36;break;
                case 55:shortValue[i]= 0x37;break;
                case 56:shortValue[i]= 0x38;break;
                case 57:shortValue[i]= 0x39;break;
                case 58:shortValue[i]= 0x3A;break;
                case 59:shortValue[i]= 0x3B;break;
                case 60:shortValue[i]= 0x3C;break;
                case 61:shortValue[i]= 0x3D;break;
                case 62:shortValue[i]= 0x3E;break;
                case 63:shortValue[i]= 0x3F;break;
                case 64:shortValue[i]= 0x40;break;
                case 65:shortValue[i]= 0x41;break;
                case 66:shortValue[i]= 0x42;break;
                case 67:shortValue[i]= 0x43;break;
                case 68:shortValue[i]= 0x44;break;
                case 69:shortValue[i]= 0x45;break;
                case 70:shortValue[i]= 0x46;break;
                case 71:shortValue[i]= 0x47;break;
                case 72:shortValue[i]= 0x48;break;
                case 73:shortValue[i]= 0x49;break;
                case 74:shortValue[i]= 0x4A;break;
                case 75:shortValue[i]= 0x4B;break;
                case 76:shortValue[i]= 0x4C;break;
                case 77:shortValue[i]= 0x4D;break;
                case 78:shortValue[i]= 0x4E;break;
                case 79:shortValue[i]= 0x4F;break;
                case 80:shortValue[i]= 0x50;break;
                case 81:shortValue[i]= 0x51;break;
                case 82:shortValue[i]= 0x52;break;
                case 83:shortValue[i]= 0x53;break;
                case 84:shortValue[i]= 0x54;break;
                case 85:shortValue[i]= 0x55;break;
                case 86:shortValue[i]= 0x56;break;
                case 87:shortValue[i]= 0x57;break;
                case 88:shortValue[i]= 0x58;break;
                case 89:shortValue[i]= 0x59;break;
                case 90:shortValue[i]= 0x5A;break;
                case 91:shortValue[i]= 0x5B;break;
                case 92:shortValue[i]= 0x5C;break;
                case 93:shortValue[i]= 0x5D;break;
                case 94:shortValue[i]= 0x5E;break;
                case 95:shortValue[i]= 0x5F;break;
                case 96:shortValue[i]= 0x60;break;
                case 97:shortValue[i]= 0x61;break;
                case 98:shortValue[i]= 0x62;break;
                case 99:shortValue[i]= 0x63;break;
                case 100:shortValue[i]= 0x64;break;
                case 101:shortValue[i]= 0x65;break;
                case 102:shortValue[i]= 0x66;break;
                case 103:shortValue[i]= 0x67;break;
                case 104:shortValue[i]= 0x68;break;
                case 105:shortValue[i]= 0x69;break;
                case 106:shortValue[i]= 0x6A;break;
                case 107:shortValue[i]= 0x6B;break;
                case 108:shortValue[i]= 0x6C;break;
                case 109:shortValue[i]= 0x6D;break;
                case 110:shortValue[i]= 0x6E;break;
                case 111:shortValue[i]= 0x6F;break;
                case 112:shortValue[i]= 0x70;break;
                case 113:shortValue[i]= 0x71;break;
                case 114:shortValue[i]= 0x72;break;
                case 115:shortValue[i]= 0x73;break;
                case 116:shortValue[i]= 0x74;break;
                case 117:shortValue[i]= 0x75;break;
                case 118:shortValue[i]= 0x76;break;
                case 119:shortValue[i]= 0x77;break;
                case 120:shortValue[i]= 0x78;break;
                case 121:shortValue[i]= 0x79;break;
                case 122:shortValue[i]= 0x7A;break;
                case 123:shortValue[i]= 0x7B;break;
                case 124:shortValue[i]= 0x7C;break;
                case 125:shortValue[i]= 0x7D;break;
                case 126:shortValue[i]= 0x7E;break;
                case 127:shortValue[i]= 0x7F;break;
                case 128:shortValue[i]= 0x80;break;
                case 129:shortValue[i]= 0x81;break;
                case 130:shortValue[i]= 0x82;break;
                case 131:shortValue[i]= 0x83;break;
                case 132:shortValue[i]= 0x84;break;
                case 133:shortValue[i]= 0x85;break;
                case 134:shortValue[i]= 0x86;break;
                case 135:shortValue[i]= 0x87;break;
                case 136:shortValue[i]= 0x88;break;
                case 137:shortValue[i]= 0x89;break;
                case 138:shortValue[i]= 0x8A;break;
                case 139:shortValue[i]= 0x8B;break;
                case 140:shortValue[i]= 0x8C;break;
                case 141:shortValue[i]= 0x8D;break;
                case 142:shortValue[i]= 0x8E;break;
                case 143:shortValue[i]= 0x8F;break;
                case 144:shortValue[i]= 0x90;break;
                case 145:shortValue[i]= 0x91;break;
                case 146:shortValue[i]= 0x92;break;
                case 147:shortValue[i]= 0x93;break;
                case 148:shortValue[i]= 0x94;break;
                case 149:shortValue[i]= 0x95;break;
                case 150:shortValue[i]= 0x96;break;
                case 151:shortValue[i]= 0x97;break;
                case 152:shortValue[i]= 0x98;break;
                case 153:shortValue[i]= 0x99;break;
                case 154:shortValue[i]= 0x9A;break;
                case 155:shortValue[i]= 0x9B;break;
                case 156:shortValue[i]= 0x9C;break;
                case 157:shortValue[i]= 0x9D;break;
                case 158:shortValue[i]= 0x9E;break;
                case 159:shortValue[i]= 0x9F;break;
                case 160:shortValue[i]= 0xA0;break;
                case 161:shortValue[i]= 0xA1;break;
                case 162:shortValue[i]= 0xA2;break;
                case 163:shortValue[i]= 0xA3;break;
                case 164:shortValue[i]= 0xA4;break;
                case 165:shortValue[i]= 0xA5;break;
                case 166:shortValue[i]= 0xA6;break;
                case 167:shortValue[i]= 0xA7;break;
                case 168:shortValue[i]= 0xA8;break;
                case 169:shortValue[i]= 0xA9;break;
                case 170:shortValue[i]= 0xAA;break;
                case 171:shortValue[i]= 0xAB;break;
                case 172:shortValue[i]= 0xAC;break;
                case 173:shortValue[i]= 0xAD;break;
                case 174:shortValue[i]= 0xAE;break;
                case 175:shortValue[i]= 0xAF;break;
                case 176:shortValue[i]= 0xB0;break;
                case 177:shortValue[i]= 0xB1;break;
                case 178:shortValue[i]= 0xB2;break;
                case 179:shortValue[i]= 0xB3;break;
                case 180:shortValue[i]= 0xB4;break;
                case 181:shortValue[i]= 0xB5;break;
                case 182:shortValue[i]= 0xB6;break;
                case 183:shortValue[i]= 0xB7;break;
                case 184:shortValue[i]= 0xB8;break;
                case 185:shortValue[i]= 0xB9;break;
                case 186:shortValue[i]= 0xBA;break;
                case 187:shortValue[i]= 0xBB;break;
                case 188:shortValue[i]= 0xBC;break;
                case 189:shortValue[i]= 0xBD;break;
                case 190:shortValue[i]= 0xBE;break;
                case 191:shortValue[i]= 0xBF;break;
                case 192:shortValue[i]= 0xC0;break;
                case 193:shortValue[i]= 0xC1;break;
                case 194:shortValue[i]= 0xC2;break;
                case 195:shortValue[i]= 0xC3;break;
                case 196:shortValue[i]= 0xC4;break;
                case 197:shortValue[i]= 0xC5;break;
                case 198:shortValue[i]= 0xC6;break;
                case 199:shortValue[i]= 0xC7;break;
                case 200:shortValue[i]= 0xC8;break;
                case 201:shortValue[i]= 0xC9;break;
                case 202:shortValue[i]= 0xCA;break;
                case 203:shortValue[i]= 0xCB;break;
                case 204:shortValue[i]= 0xCC;break;
                case 205:shortValue[i]= 0xCD;break;
                case 206:shortValue[i]= 0xCE;break;
                case 207:shortValue[i]= 0xCF;break;
                case 208:shortValue[i]= 0xD0;break;
                case 209:shortValue[i]= 0xD1;break;
                case 210:shortValue[i]= 0xD2;break;
                case 211:shortValue[i]= 0xD3;break;
                case 212:shortValue[i]= 0xD4;break;
                case 213:shortValue[i]= 0xD5;break;
                case 214:shortValue[i]= 0xD6;break;
                case 215:shortValue[i]= 0xD7;break;
                case 216:shortValue[i]= 0xD8;break;
                case 217:shortValue[i]= 0xD9;break;
                case 218:shortValue[i]= 0xDA;break;
                case 219:shortValue[i]= 0xDB;break;
                case 220:shortValue[i]= 0xDC;break;
                case 221:shortValue[i]= 0xDD;break;
                case 222:shortValue[i]= 0xDE;break;
                case 223:shortValue[i]= 0xDF;break;
                case 224:shortValue[i]= 0xE0;break;
                case 225:shortValue[i]= 0xE1;break;
                case 226:shortValue[i]= 0xE2;break;
                case 227:shortValue[i]= 0xE3;break;
                case 228:shortValue[i]= 0xE4;break;
                case 229:shortValue[i]= 0xE5;break;
                case 230:shortValue[i]= 0xE6;break;
                case 231:shortValue[i]= 0xE7;break;
                case 232:shortValue[i]= 0xE8;break;
                case 233:shortValue[i]= 0xE9;break;
                case 234:shortValue[i]= 0xEA;break;
                case 235:shortValue[i]= 0xEB;break;
                case 236:shortValue[i]= 0xEC;break;
                case 237:shortValue[i]= 0xED;break;
                case 238:shortValue[i]= 0xEE;break;
                case 239:shortValue[i]= 0xEF;break;
                case 240:shortValue[i]= 0xF0;break;
                case 241:shortValue[i]= 0xF1;break;
                case 242:shortValue[i]= 0xF2;break;
                case 243:shortValue[i]= 0xF3;break;
                case 244:shortValue[i]= 0xF4;break;
                case 245:shortValue[i]= 0xF5;break;
                case 246:shortValue[i]= 0xF6;break;
                case 247:shortValue[i]= 0xF7;break;
                case 248:shortValue[i]= 0xF8;break;
                case 249:shortValue[i]= 0xF9;break;
                case 250:shortValue[i]= 0xFA;break;
                case 251:shortValue[i]= 0xFB;break;
                case 252:shortValue[i]= 0xFC;break;
                case 253:shortValue[i]= 0xFD;break;
                case 254:shortValue[i]= 0xFE;break;
                case 255:shortValue[i]= 0xFF;break;
            }
        }
        return shortValue;
    }






}
