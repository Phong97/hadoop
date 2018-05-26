package mypack;

import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
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
	private static ArrayList<String> keyText;
	// Compenents
	public JFrame Frame;
	private JTextField Path;
	static JTextField keysearch;
	private Button BtnChonFile;
	private Button BtnRun;
	private Button BtnAddFile;
	private Button BtnResetFile;

	// cÃ¡c textArea vÃ  label hiá»ƒn thá»‹ káº¿t quáº£
	private TextArea lbDanhSachFile, lbDanhSachKQ;
	private JLabel lb1, lb3,lbkeysearch;

	// cÃ¡c biáº¿n khá»Ÿi táº¡o
	public String filename = null;
	public static Path[] input = new Path[10];
	public ArrayList<String> Multifile = new ArrayList<>();

	// khai bÃ¡o
	public static Configuration c;
	public static String getFile = "";

	// hÃ m main
	public static void main(String[] args) throws Exception {
		WordCount wordcount = new WordCount();
		wordcount.GUI(args);

	}

	public void GUI(String[] args) {
		// Đường dẫn file
		Path = new JTextField();
		Path.setBounds(200, 30, 200, 20);
		Path.setLayout(null);
		//key search
		keysearch=new JTextField();
		keysearch.setBounds(200, 50, 200, 20);
		//label keysearch
		lbkeysearch=new JLabel("Từ khóa");
		lbkeysearch.setBounds(140,50,50,20);
		// button Browers
		BtnChonFile = new Button("Chọn File");
		BtnChonFile.setBounds(410, 30, 80, 20);

		// button add
		BtnAddFile = new Button("Add");
		BtnAddFile.setBounds(200, 80, 50, 20);

		// button add reset file
		BtnResetFile = new Button("Reset");
		BtnResetFile.setBounds(270, 80, 50, 20);

		// button thÃ´ng kÃª
		BtnRun = new Button("Thực Thi");
		BtnRun.setBounds(70, 110, 100, 20);

		// hien thi
		lb1 = new JLabel("Danh Sách File:");
		lb1.setBounds(20, 130, 100, 20);
		lb1.setLayout(null);

		// Danh sách files
		lbDanhSachFile = new TextArea("");
		lbDanhSachFile.setBounds(20, 150, 200, 250);
		//
		lb3 = new JLabel("Kết Quả");
		lb3.setBounds(250, 130, 80, 20);
		lb3.setLayout(null);
		// Danh sách kết quả
		lbDanhSachKQ = new TextArea("");
		lbDanhSachKQ.setBounds(250, 150, 360, 250);
		// Frame
		Frame = new JFrame("");
		Frame.setTitle("Word Files by Nhom 12");
		Frame.setBounds(500, 200, 650, 460);
		Frame.setLayout(null);
		Frame.setVisible(true);
		Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set giao diện
		Frame.add(Path);
		Frame.add(keysearch);
		Frame.add(lbkeysearch);
		Frame.add(BtnChonFile);
		Frame.add(BtnAddFile);
		Frame.add(BtnResetFile);
		Frame.add(BtnRun);
		Frame.add(lb1);
		Frame.add(lbDanhSachFile);
		Frame.add(lb3);
		Frame.add(lbDanhSachKQ);

		// 
		BtnRun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Reset
				lbDanhSachKQ.setText("");

				// Kiểm tra đã đưa file vào chưa
				if (Multifile.size() == 0) {
					JOptionPane.showMessageDialog(Frame,
							"Hãy chọn các file text");
					return;
				}

				// xóa các file đã tồn tại nếu có
				File file = new File("src/output");
				if (file.isDirectory()) {
					String[] children = file.list();
					for (int i = 0; i < children.length; i++) {
						File filechildren = new File(file, children[i]);
						filechildren.delete();
					}
					file.delete();
					System.out.println("XÃ³a thÃ nh cÃ´ng thÆ° má»¥c");
				}

				// lấy đường dẫn của cac file đưa vào
				for (int i = 0; i < Multifile.size(); i++) {
					input[i] = new Path(Multifile.get(i));
				}

				// Thực hiền map reduce
				try {
					MapReduce(input);
				} catch (ClassNotFoundException e1) {

					e1.printStackTrace();
				} catch (IOException e1) {

					e1.printStackTrace();
				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}

				//đọc file sau khi xử lí
				BufferedReader br = null;
				String kq = "";
				try {
					br = new BufferedReader(new FileReader("src/output/part-r-00000"));

					String textInALine = br.readLine();
					while (textInALine != null) {
						kq += textInALine + "\n";
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
		if (e.getSource() == BtnChonFile) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "All Files", ".txt");
			fileChooser.setFileFilter(filter);

			int i = fileChooser.showOpenDialog(null);
			if (i == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				filename = file.getAbsolutePath();
				try {
					Path.setText(filename);
				} catch (Exception e2) {

				}
			}
		}

		if (e.getSource() == BtnAddFile) {
			if (Path.getText().equals("")) {
				JOptionPane.showMessageDialog(Frame, "Xin chá»�n file");
			} else {
				Multifile.add(Path.getText());
				getFile += Path.getText() + "\n";
				Path.setText("");
				lbDanhSachFile.setText(getFile);
			}
		}

		if (e.getSource() == BtnResetFile) {
			Multifile.removeAll(Multifile);
			lbDanhSachFile.setText("");
			getFile = "";
		}
	}

	//  Map_Reduce
	public void MapReduce(Path[] input) throws IOException, ClassNotFoundException, InterruptedException {
		//láy các từ khóa
		keyText = new ArrayList<String>();
		String[] keys = keysearch.getText().split("[ ,.?!()\r\\n\"\\']");
		for (String key : keys) {
			keyText.add(key.toUpperCase());
		}
		Configuration c = new Configuration();
		Path output = new Path("src/output");
		Job j = new Job(c, "wordcount");
		j.setJarByClass(WordCount.class);
		j.setMapperClass(MapForWordCount.class);
		j.setReducerClass(ReduceForWordCount.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		for (int i = 0; i < Multifile.size(); i++) {
			FileInputFormat.addInputPath(j, input[i]);

		}
		FileOutputFormat.setOutputPath(j, output);
		j.waitForCompletion(true);
	}

	// Ham Map()
	public static class MapForWordCount extends Mapper<LongWritable, Text, Text, IntWritable> {
		public void map(LongWritable key, Text value, Context con) throws IOException, InterruptedException {
			ArrayList<String> d = (ArrayList<String>) keyText.clone();
			String line = value.toString();
			// cắt các từ 
			String[] words = line.split("[ ,.?!()\r\\n\"\\']");
			for (String word : words) {
				Text outputKey = new Text(word.toUpperCase().trim());
				if (d.indexOf(outputKey.toString()) != -1) {
					d.remove(outputKey.toString());
					IntWritable outputValue = new IntWritable(1);
					con.write(outputKey, outputValue);
				}
			}
		}
	}

	//  Reduce()
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
