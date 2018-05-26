# hadoop trên Windows
# wordcount1: đếm số lượng lần xuất hiện của từ.
# wordcount2: đếm số lượng file xuất hiện của từ.

1.Các bước chuẩn bị:
  -	Các phần mềm cần chuẩn bị để cài đặt Hadoop 2.7.3 trên windows 8.1 64bit
    •	Hadoop 2.7.3.tar.gz 
    (Link: http://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.7.3/hadoop-2.7.3.tar.gz )
    •	Java jdk 1.8.0.zip 
    (Link: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html )
    •	Hadoop-eclipse-plugin-2.6.0.jar 
    (Link: https://github.com/winghc/hadoop2x-eclipse-plugin/tree/master/release )
2. Cài đặt hadoop:
  Bước 1: 
-	Giải nén file Hadoop 2.7.3.tar.gz
-	Thiết lập đường dẫn HADOOP_HOME ,JAVA_HOME biến môi trường trong windows
-	Tiếp theo chúng ta đặt đường dẫn thư mục hadoop bin, java bin
Bước 2: Cấu hình Hadoop
•	Sửa file hadoop-2.7.3/etc/hadoop/core-site.xml, dán các dòng bên dưới vào và lưu lại: 
<configuration>
   <property>
       <name>fs.defaultFS</name>
       <value>hdfs://localhost:9000</value>
   </property>
</configuration>
•	Sửa file hadoop-2.7.3/etc/hadoop/mapred-site.xml, dán các dòng bên dưới vào và lưu lại: 
<configuration>
   <property>
       <name>mapreduce.framework.name</name>
       <value>yarn</value>
   </property>
</configuration>
•	Tạo folder data trong Hadoop
•	Sửa file  hadoop-2.7.3/etc/hadoop/hdfs-site.xml, dán các dòng bên dưới vào và lưu lại. 
<configuration>
   <property>
       <name>dfs.replication</name>
       <value>1</value>
   </property>
   <property>
       <name>dfs.namenode.name.dir</name>
       <value>C:\hadoop-2.7.3\data\namenode</value>
   </property>
   <property>
       <name>dfs.datanode.data.dir</name>
     <value>C:\hadoop-2.7.3\data\datanode</value>
   </property>
</configuration>
•	Sửa file  hadoop-2.7.3/etc/hadoop/yarn-site.xml, dán các dòng bên dưới vào và lưu lại. 
<configuration>
   <property>
       <name>yarn.nodemanager.aux-services</name>
       <value>mapreduce_shuffle</value>
   </property>
   <property>
      <name>yarn.nodemanager.auxservices.mapreduce.shuffle.class
</name>  
<value>org.apache.hadoop.mapred.ShuffleHandler</value>
   </property>
</configuration>
•	Sửa file hadoop/etc/hadoop/hadoop-env.cmd bằng cách đóng dòng lệnh JAVA_HOME=%JAVA_HOME% thay bằng set JAVA_HOME=C:\java (Trong C:\java này là đường dẫn đến file jdk.18.0)
Bước 3: 
-	Bước tiếp theo: Dowload file sardetushar_gitrepo_download theo link: https://github.com/sardetushar/hadooponwindows/archive/master.zip
Và tiến hành xóa file bin trong C:\hadoop\bin , thay bằng file bin trong file vừa mới download về.
-	Mở cmd và gõ lệnh “hdfs namenode –format” để định dạng namenode.
-	Dùng cmd chuyển đến thư mục sbin và nhấn “start-all.cmd” để khởi động apache.
-	Nó sẽ bắt đầu quá trình sau đây
 	Namenode
 	Datanode
 	YARN resourcemanager
 	YARN nodemanager
-	Tiếp tục gõ jps để chắc chắn rằng đường dẫn java của bạn đã chính xác. 
 Bước 4: 
-	Mở giao diện người dùng cho Resourcemanager với địa chỉ:  http://localhost:8088
-	Mở giao diện người dùng cho Namenode với địa chỉ:  http://localhost:50070
###### => Như vậy Hadoop đã cài thành công.

# Cài đặt và chạy ứng dụng map reduce với bài toán wordcount:
Sau khi cài thành công Hadoop chúng ta tiến hành cài đặt Map reduce trên eclipse.
Bước 1: 
-	Cài đặt Hadoop-eclipse-plugin-2.6.0.jar bằng cách copy file và dán vào plugins của file eclipse và file contrib của hadoop (phải tạo sẵn file contrib trong hadoop).
-	Cài đặt eclipse: Chạy file eclipse.exe trong file eclipse. Chọn workspace là C:\workspace.
-	Ở góc phải eclipse. Bấm vào biểu tượng như bước 1 bên hình dưới và làm theo bước 2, 3: Để cài đặt Map/Reduce. Chúng ta sẽ thấy DFS Locations bên dưới Project Explorer của chương trình eclipse
-	Tạo một Map/ Reduce mới. Vào File -> New -> Other -> Map/ Reduce Project. Sau đó bấm Next
-	Đặt tên project và cấu hình thư mục Hadoop là ở nơi chứa hadoop (ví dụ: C:\hadoop-2.7.3)
-	Bây giờ bạn sẽ tạo 1 class trong src bằng cách nhấn chuột phải vào src -> New -> Class và đặt tên là Word_count
-	Hệ thống sẽ tự khởi tạo lớp. Chúng ta sẽ bắt đầu viết chương trình Wordcount.
-	Chương trình gồm 2 hàm chính là hàm Map và hàm Reduce.
-	Sau khi chương trình đã viết hoàn tất. Chúng ta vào Run -> Run Configurations -> Arguments để cấu hình file đầu vào và file đầu ra. Và bấm Apply.
-	Cuối cùng chúng ta vào Run -> Run as -> Run on Hadoop.
-	À quên các file và thư mục cấu hình đầu vào đầu ra sẽ phải nằm trong project folder(ví dụ: workspace\ wordcount)
-	Kết quả được tạo ra trong file part-r-00000 trong file output.
