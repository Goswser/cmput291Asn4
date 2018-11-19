import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {
		
		//check file input
		if (args.length < 1) {
			System.out.println("input file not detected");
			System.exit(1);
		}
		
		try {
			File file = new File(args[0]);
			Scanner fp = new Scanner(file);
			
			while(fp.hasNextLine()) {
				String line = fp.nextLine();
				parseLine(line);
			}
			fp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void parseLine(String line) {
		String price = null;
		//if this doesnt work, use regex indices
		String Oad = "<ad>";
		String Cad = "</ad>";
		
		Matcher m = Pattern.compile(Pattern.quote(Oad) + "(.*?)" +  Pattern.quote(Cad)).matcher(line);
		//Matcher m = p.matcher(line);
		if (!m.find()) {
			return;
		}
		String ad = m.group(1);
		
		// find aid
		String Oaid = "<aid>";
		String Caid = "</aid>";
		Matcher Maid = Pattern.compile(Pattern.quote(Oaid) + "(.*?)" + Pattern.quote(Caid)).matcher(line);
	
		if (!Maid.find()) {
			return;
		}
		
		String aid = Maid.group(1);
		
		// find date
		String Odate = "<date>";
		String Cdate = "</date>";
		Matcher Mdate = Pattern.compile(Pattern.quote(Odate) + "(.*?)" + Pattern.quote(Cdate)).matcher(ad);
		
		if (!Mdate.find()) {
			return;
		}
		
		String date = Mdate.group(1);
		
		// find location
		String Oloc = "<loc>";
		String Cloc = "</loc>";
		Matcher Mloc = Pattern.compile(Pattern.quote(Oloc) + "(.*?)" + Pattern.quote(Cloc)).matcher(ad);
		
		if (!Mloc.find()) {
			return;
		}
		
		String loc = Mloc.group(1);
		
		// find category
		String Ocat = "<cat>";
		String Ccat = "</cat>";
		Matcher Mcat = Pattern.compile(Pattern.quote(Ocat) + "(.*?)" + Pattern.quote(Ccat)).matcher(ad);
		
		if (!Mcat.find()) {
			return;
		}
		
		String cat = Mcat.group(1);
		
		//find description
		String Odesc = "<desc>";
		String Cdesc = "</desc>";
		Matcher Mdesc = Pattern.compile(Pattern.quote(Odesc) + "(.*?)" + Pattern.quote(Cdesc)).matcher(ad);
		
		if (!Mdesc.find()) {
			return;
		}
		
		String desc = Mdesc.group(1);
		
		//find title
		String Oti = "<ti>";
		String Cti = "</ti>";
		Matcher Mtitle = Pattern.compile(Pattern.quote(Oti) + "(.*?)" + Pattern.quote(Cti)).matcher(ad);
		
		if (!Mtitle.find()) {
			return;
		}
		
		String title = Mtitle.group(1);
		
		//find price
		String Oprice = "<price>";
		String Cprice = "</price>";
		Matcher Mprice = Pattern.compile(Pattern.quote(Oprice) + "(.*?)" + Pattern.quote(Cprice)).matcher(ad);
		
		if (Mprice.find()) {
			price = Mprice.group(1);
		} 
		
		
		try {
			terms(title, desc, aid);
			pdates(date, cat, loc, aid);
			prices(price, cat, loc, aid);
			ads(aid, line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private static String ignoreSpecial(String s) {
		String p1 = "&";
		String p2 = ";";
		Matcher m = Pattern.compile("(" + Pattern.quote(p1) + "(.*?)" + Pattern.quote(p2) + ")").matcher(s);
		if (!m.find()) {
			return s;
		}
		return s.replaceAll(m.group(1), "");
	}
	
	private static String ParseString(String s) {
		s = ignoreSpecial(s);
		char[] w = s.toCharArray();
		
		for(char ch : w) {
			if ((ch < 'A' && ch > 'Z' && ch < 'a' && ch > 'z' && ch < '0' && ch >'9') && (ch != '_' && ch != '-')) {
				//extra filter i guess
			}
		}
		return s;
	}
	
	private static void terms(String title, String desc, String aid) throws IOException {
		//CHECK FOR SPICIFIC REGEX
		//template for creating file or find another on stackoverflow
		List<String> lines = new ArrayList<>();
		List<String> terms = new ArrayList<>();
		
		if (title.contains(" ")) {
			String words[] = title.trim().replaceAll("[\\,.]" , "").split("\\s+");
			System.out.println(words.length);
			for (int i = 0; i < words.length; i++) {
				if (words[i].length() > 2) {
					terms.add(ParseString(words[i]).toLowerCase());
				}
			}
		} else {
			terms.add(ParseString(title).toLowerCase());
		}
		
		if (desc.contains(" ")) {
			String words[] = desc.trim().replaceAll("[\\,.]" , "").split("\\s+");
			for (int i = 0; i < words.length; i++) {
				if (words[i].length() > 2) {
					terms.add(ParseString(words[i]).toLowerCase());
				}
			}
		} else {
			terms.add(ParseString(desc).toLowerCase());
		}
					
		for (String s : terms) {
			lines.add(s + ":" + aid + "\n");
		}
		
		
		Path file = Paths.get("../terms.txt");
		Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}
	
	private static void pdates(String date, String cat, String loc, String aid) throws IOException {
		List<String> lines = new ArrayList<>();
		
		lines.add(date + ":" + aid + "," + cat + "," + loc + "\n");
		
		Path file = Paths.get("../pdates.txt");
		Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}
	
	private static void prices(String price, String cat, String loc, String aid) throws IOException {
		if(price == null) {
			return;
		}
		List<String> lines = new ArrayList<>();
		
		//TODO check this for non empty price before price is passed to this function
		
		lines.add(price + ":" + aid + "," + cat + "," + loc + "\n");
		
		Path file = Paths.get("../prices.txt");
		Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}
	
	private static void ads(String aid, String line) throws IOException {
		List<String> lines = new ArrayList<>();
		
		lines.add(aid + ":" + line + "\n");
		
		Path file = Paths.get("../ads.txt");
		Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}
}
