package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.internous.ecsite.model.dao.GoodsRepository;
import jp.co.internous.ecsite.model.dao.UserRepository;
import jp.co.internous.ecsite.model.entity.Goods;
import jp.co.internous.ecsite.model.entity.User;
import jp.co.internous.ecsite.model.form.GoodsForm;
import jp.co.internous.ecsite.model.form.LoginForm;

@Controller
@RequestMapping("/ecsite/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepos;
	
	@Autowired
	private GoodsRepository goodsRepos;
	
	@RequestMapping("/")
	public String index() {
		return "adminindex";
	}
	
	/* 設問1変更点 
	*	welcomeメソッドの戻り値となる String型変数 returnPage を宣言して初期化
	*	47行目if文内をswitch文に変更（if文でラベルを用いた制御に自信がなかったため）
	*	管理者権限を持つアカウントでのログインの場合は、変更前と同様
	*	それ以外の場合は、変数retrunPageに"adminindex"を代入し、
	*		adminindex.htmlで赤字文表示するための変数　isWrong に1を代入（ここが一番ごり押しでよくないと思っております）
	*	adminindex.htmlに制御が移る
	*/	
	@PostMapping("/welcome")
	public String welcome(LoginForm form, Model m) {
		List<User> users = userRepos.findByUserNameAndPassword(form.getUserName(), form.getPassword());
		String returnPage = null;
		if(users != null && users.size() > 0) {
			int Admin = users.get(0).getIsAdmin();
			switch(Admin) {
				case 1:
					List<Goods> goods = goodsRepos.findAll();
					m.addAttribute("userName", users.get(0).getUserName());
					m.addAttribute("password", users.get(0).getPassword());
					m.addAttribute("goods", goods);
					System.out.println(form.getUserName() + " " + form.getPassword());
					returnPage = "welcome";
					break;
				case 0:
					System.out.println(form.getUserName() + " " + form.getPassword());
					returnPage = "adminindex";
					m.addAttribute("isError", 1);
					break;
			} 
		} else {
			System.out.println(form.getUserName() + " " + form.getPassword());
			returnPage = "adminindex";
			m.addAttribute("isError", 1);
		}
		return returnPage;
	}
	
	@RequestMapping("/goodsMst")
	public String goodsMst(LoginForm form, Model m) {
		m.addAttribute("userName", form.getUserName());
		m.addAttribute("password", form.getPassword());
		return "goodsmst";
	}
	
	@RequestMapping("/addGoods")
	public String addGoods(GoodsForm goodsForm, LoginForm loginForm, Model m) {
		m.addAttribute("userName", loginForm.getUserName());
		m.addAttribute("password", loginForm.getPassword());
		
		Goods goods = new Goods();
		goods.setGoodsName(goodsForm.getGoodsName());
		goods.setPrice(goodsForm.getPrice());
		goodsRepos.saveAndFlush(goods);
		
		return "forward:/ecsite/admin/welcome";
	}
	
	@ResponseBody
	@PostMapping("/api/deleteGoods")
	public String deleteApi(@RequestBody GoodsForm f, Model m) {
		try {
			goodsRepos.deleteById(f.getId());
		} catch (IllegalArgumentException e) {
			return "-1";
		}
		return "1";
	}

}
