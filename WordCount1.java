package mypack;

import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount implements ActionListener {
	// các frame và button
		public JFrame Frame;
		private JTextField Path;
		private Button BtnChonFile;
		private Button BtnRun;
		private Button BtnAddFile;
		private Button BtnResetFile;
		
		// các textArea và label hiển thị kết quả 
		private TextArea lbDanhSachFile,lbDanhSachKQ;
		private JLabel lb1,lb3;
		
		// các biến khởi tạo
		public String filename = null;
		public static Path[] input =new Path[10];
		public ArrayList<String> Multifile=new ArrayList<>();
		
		//khai báo
		public static Configuration c;
		public static String getFile="";
		
		//hàm main
		public static void main(String[] args) throws Exception {
			WordCount wordcount=new WordCount();
			wordcount.GUI(args);

			
		}
		public void GUI(String[] args)
		{
			// đường dẫn 
			Path=new JTextField();
			Path.setBounds(200,30 , 200, 20);
			Path.setLayout(null);
			
			// button chọn đường dẫn 
			BtnChonFile=new Button("Chọn File");
			BtnChonFile.setBounds(410, 30, 80, 20);
			
			//button add nhiều file 
			BtnAddFile =new Button("Add");
			BtnAddFile.setBounds(200, 60, 50, 20);
			
			//button add reset file
			BtnResetFile =new Button("Reset");
			BtnResetFile.setBounds(270, 60, 50, 20);
			
			//button thông kê
			BtnRun =new Button("Thống kê");
			BtnRun.setBounds(70,90, 100, 20);
			
			// hien thi 
			lb1=new JLabel("Danh sách File:");
			lb1.setBounds(20, 110, 100, 20);
			lb1.setLayout(null);
			
			//Danh sách file can thong ke
			lbDanhSachFile=new TextArea("");
			lbDanhSachFile.setBounds(20, 130, 200, 250);
			//
			lb3=new JLabel("Kết Quả");
			lb3.setBounds(250, 110, 80, 20);
			lb3.setLayout(null);
			// Danh sách Kết quả 
			lbDanhSachKQ=new TextArea("");
			lbDanhSachKQ.setBounds(250, 130, 360, 250);
			// Frame
			Frame = new JFrame("");
			Frame.setTitle("Word Count by Nhom 17");
			Frame.setBounds(500, 200, 650, 440);
		    Frame.setLayout(null);
		    Frame.setVisible(true);
		    Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    
		    // thêm vào Frame
		    Frame.add(Path);
		    Frame.add(BtnChonFile);
		    Frame.add(BtnAddFile);
		    Frame.add(BtnResetFile);
		    Frame.add(BtnRun);
		    Frame.add(lb1);
		    Frame.add(lbDanhSachFile);
		    Frame.add(lb3);
		    Frame.add(lbDanhSachKQ);
		    
		    //Xử lý sự kiện click
		    BtnRun.addActionListener(new ActionListener() {
		 	   
				@Override
				public void actionPerformed(ActionEvent e) {
					
					//Reset kết quả 
					lbDanhSachKQ.setText("");
					
					// Kiểm tra xem có chọn file chưa
					if(Multifile.size()==0)
					{
						JOptionPane.showMessageDialog(Frame, "Xin chọn file .txt để thực hiện Thống kê số từ khóa xuất hiện trong các documents");
						return;
					}
					
					// Xóa file output nếu tồn tại
					File file = new File("src/output");
					if (file.isDirectory())
					{
						 String[] children = file.list();
				         for (int i = 0; i < children.length; i++) {
				        	 File filechildren=new File(file, children[i]);
				        	 filechildren.delete();
				         }
						file.delete();
						System.out.println("Xóa thành công thư mục rỗng");
					}
					
					// Đưa các file txt vào 
					for(int i = 0;i<Multifile.size();i++)
					{
						input[i]=new Path(Multifile.get(i));
					}
					
					//Thực hiện Map-Reduce
					try 
					{
						MapReduce(input);
					} 
					catch (ClassNotFoundException e1) {
						
						e1.printStackTrace();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						
						e1.printStackTrace();
					}
					
					// Đọc file part-r-00000 vào chỗ hiển thị kết quả 
					BufferedReader br = null;
					String kq="";
			        try {   
			            br = new BufferedReader(new FileReader("src/output/part-r-00000"));       
			            
			            String textInALine=br.readLine();
			            while (textInALine != null) {		
			                kq+=textInALine+"\n";
			                textInALine = br.readLine();
			            }
			            
			            lbDanhSachKQ.setText(kq.toString());

			        } catch (IOException e1) {
			            e1.printStackTrace();
			        } finally {
			            try {
			                br.close();
			            } catch (IOException e1) {
			                e1.printStackTrace();
			            }
			        }
			        
				}				 
			});
		    
		    BtnAddFile.addActionListener(this);
		    BtnChonFile.addActionListener(this);
		    BtnResetFile.addActionListener(this);
		    
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==BtnChonFile)
			{
				JFileChooser fileChooser = new JFileChooser();
				 FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "All Files", ".txt");
			    fileChooser.setFileFilter(filter);

			    int i = fileChooser.showOpenDialog(null);
			    if (i == JFileChooser.APPROVE_OPTION)
			    {
			     File file = fileChooser.getSelectedFile();
			     filename = file.getAbsolutePath();
				     try {
				       Path.setText(filename);
				     }
				     catch (Exception e2) {
				     
				     }
			   }
			}
			
			if(e.getSource()==BtnAddFile)
			{
				if(Path.getText().equals(""))
				{
					JOptionPane.showMessageDialog(Frame, "Xin chọn file");
				}		
				else
				{
					Multifile.add(Path.getText());
					getFile+=Path.getText()+"\n";
					Path.setText("");	
					lbDanhSachFile.setText(getFile);
				}
			}
		
			if(e.getSource()==BtnResetFile)
			{
				Multifile.removeAll(Multifile);
				lbDanhSachFile.setText("");
				getFile="";
			}					
		}
		
		// Xử lý Map_Reduce
		public void MapReduce(Path[] input) throws IOException, ClassNotFoundException, InterruptedException
		{
			Configuration c = new Configuration();
			Path output = new Path("src/output");
			Job j = new Job(c, "wordcount");
			j.setJarByClass(WordCount.class);
			j.setMapperClass(MapForWordCount.class);
			j.setReducerClass(ReduceForWordCount.class);
			j.setOutputKeyClass(Text.class);
			j.setOutputValueClass(IntWritable.class);
			for(int i=0 ;i<Multifile.size();i++)
			{
			FileInputFormat.addInputPath(j, input[i]);
				
			}
			FileOutputFormat.setOutputPath(j, output);
			j.waitForCompletion(true);
		}
			
		//Ham Map()
		public static class MapForWordCount extends Mapper<LongWritable, Text, Text, IntWritable> {
			public void map(LongWritable key, Text value, Context con) throws IOException, InterruptedException {
				String line = value.toString();
				// Cắt từ với các kí hiệu sau  ,.?!()\r\\n\"\\'
				String[] words = line.split("[ ,.?!()\r\\n\"\\']");
				for (String word : words) {
					Text outputKey = new Text(word.toUpperCase().trim());
					IntWritable outputValue = new IntWritable(1);
					con.write(outputKey, outputValue);
				}
			}
		}
		
		// Hàm Reduce()
		public static class ReduceForWordCount extends Reducer<Text, IntWritable, Text, IntWritable> {
			public void reduce(Text word, Iterable<IntWritable> values, Context con)
					throws IOException, InterruptedException {
				int sum = 0;
				for (IntWritable value : values) {
					sum += value.get();
				}
				con.write(word, new IntWritable(sum));
			}
		}
}
