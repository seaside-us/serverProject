package plot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public class Plot {
	static String str = "p8:";
	// static boolean flag=false;
	static ArrayList<String> flage = new ArrayList();
	static String name;
	static ArrayList<OWLNamedClass> EmotionEvent = new ArrayList<OWLNamedClass>();
	static ArrayList<OWLNamedClass> NoEmotionEvent = new ArrayList<OWLNamedClass>();
	// static ArrayList<OWLNamedClass> Event=new ArrayList<OWLNamedClass>();
	static ArrayList ActionFrame = new ArrayList();
	static boolean NoLinkEvent = false;
	// static ArrayList<OWLIndividual> EventIndividual=new
	// ArrayList<OWLIndividual>();

	// 程序入口
	public static Document EventInfer(ArrayList<String> topiclist, ArrayList<String> actionTemplateAttr,
			OWLModel owlModel, String maName, Document doc) {
		// TODO Auto-generated method stub
		name = maName;
		EmotionEvent.clear();
		NoEmotionEvent.clear();
		ActionFrame.clear();
		flage.clear();
		NoLinkEvent = false;
		ArrayList<String> emotiontopic = new ArrayList();
		ArrayList<String> noemotiontopic = new ArrayList();
		ArrayList<String> actiontemplate = new ArrayList();
		// 判断主题中是否含有情绪主题
		OWLNamedClass emotion = owlModel.getOWLNamedClass("EmotionTopic");
		Collection emosum = emotion.getSubclasses(true);
		OWLNamedClass cla = null;
		for (int i = 0; i < topiclist.size(); i++) {
			for (Iterator in = emosum.iterator(); in.hasNext();) {
				cla = (OWLNamedClass) in.next();
				if (cla.getBrowserText().equals(topiclist.get(i))) {
					emotiontopic.add(topiclist.get(i));
					break;
				}
			}
		}
		for (int is = 0; is < topiclist.size(); is++) {
			for (int io = 0; io < emotiontopic.size(); io++) {
				if (emotiontopic.get(io).equals(topiclist.get(is))) {
					topiclist.remove(is);
				}
			}
		}
		for (int is = 0; is < topiclist.size(); is++) {
			noemotiontopic.add(topiclist.get(is));
		}

		System.out.println("emotiontopic=" + emotiontopic);
		System.out.println("noemotiontopic=" + noemotiontopic);

		ArrayList<OWLNamedClass> noEmotionEvent = new ArrayList();
		ArrayList<OWLNamedClass> emotionEvent = new ArrayList();
		ArrayList<OWLNamedClass> actionEvent = new ArrayList();
		ArrayList<String> topiclist2 = new ArrayList();
		boolean f = false;
		ArrayList<String> isEvent = new ArrayList<String>();// 判断非情绪主题是否有对应的事件
		OWLObjectProperty eventop = owlModel.getOWLObjectProperty(str + "hasSuitTopic");
		OWLObjectProperty eventop1 = owlModel.getOWLObjectProperty(str + "hasSuitTemplate");
		if (noemotiontopic.size() != 0) {
			int k = 0;

			for (Iterator it = noemotiontopic.iterator(); it.hasNext();) {
				System.out.println("第" + (++k) + "个非情绪主题");
				String noemotiontopic1 = (String) it.next();

				noEmotionEvent = getEvent1(noemotiontopic1, eventop, owlModel);
				if (noEmotionEvent.size() != 0) {
					f = true;
					topiclist2.add(noemotiontopic1);
					emotionEvent = getEvent(emotiontopic, eventop, owlModel);
					if (emotionEvent.size() == 0) {
						emotionEvent = getEventFromTemplate(actionTemplateAttr, eventop1, owlModel);
					}
					doc = setIndividual(emotiontopic, noemotiontopic, owlModel, maName, doc);// 判断主题是否有对应的人物
					doc = getPeople(owlModel, maName, noemotiontopic1, noEmotionEvent, f, doc);
				} else {
					isEvent.add("false");
				}
			}

		}
		if (noemotiontopic.size() == 0 || isEvent.size() != 0) {// 没有非情绪事件
			if (emotiontopic.size() != 0) {
				f = false;
				topiclist2.add(emotiontopic.get(emotiontopic.size() - 1));
				emotionEvent.clear();
				emotionEvent = getEvent(emotiontopic, eventop, owlModel);// 根据主题得到主要事件
				if (emotionEvent.size() != 0)
					doc = setIndividual(emotiontopic, noemotiontopic, owlModel, maName, doc);// 判断主题是否有对应的人物
				doc = getPeople(owlModel, maName, emotiontopic.get(emotiontopic.size() - 1), emotionEvent, f, doc);
			}
		}

		if (noEmotionEvent.size() == 0 && emotionEvent.size() == 0 && actionTemplateAttr.size() != 0) {

			System.out.println("开始调用模板事件");
			ArrayList<String> actionTemplate = new ArrayList<String>();
			for (Iterator ia = actionTemplateAttr.iterator(); ia.hasNext();) {
				String actionTemp = (String) ia.next().toString();
				int position = actionTemp.indexOf(":");
				String actiontempl = actionTemp.substring(position + 1);
				actiontemplate.add(actiontempl);
			}
			actionEvent = getActionEvent(actiontemplate, eventop1, owlModel);
			if (actionEvent.size() != 0)

				doc = getPeopleToTemplate(owlModel, maName, actionEvent, doc);

		}
		// 修改
		/**/
		if (flage.size() != 0) {// false表示不含主题
			System.out.println("-----------判断是否存在人物没有动作开始----------");
			for (int k = 0; k < flage.size(); k++) {
				if (flage.get(k).equals("false")) {

					if (topiclist2.size() != 0) {
						for (Iterator is = topiclist2.iterator(); is.hasNext();) {
							System.out.println(is.next());
						}

						doc = addActionToModel(owlModel, doc, topiclist2, actionTemplateAttr);

					}

					break;
				}
			}
			System.out.println("-----------判断是否存在人物没有动作结束----------");
		}

		return doc;
	}

	// 20170616
	public static Document setIndividual(ArrayList<String> emotiontopic, ArrayList<String> noemotiontopic,
			OWLModel model, String maName, Document doc) {
		int count = 0;

		OWLNamedClass AddModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty addModelNumber = model.getOWLDatatypeProperty("addModelNumber");
		OWLDatatypeProperty topicname = model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty isTemplateObject = model.getOWLDatatypeProperty("isTemplateObject");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty addModelRelatedSpace = model.getOWLObjectProperty("addModelRelatedSpace");
		OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
		OWLObjectProperty hasSubject = model.getOWLObjectProperty(str + "hasSubject");
		OWLDatatypeProperty SubjectNumber = model.getOWLDatatypeProperty(str + "SubjectNumber");
		// 20170616
		OWLDatatypeProperty isSuited = model.getOWLDatatypeProperty("isUsed");
		Collection AllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		String addmodeIsUsed = "";

		if (AllAddPeopleIndividuals.size() != 0) {
			if (noemotiontopic.size() != 0) {
				for (int ins = 0; ins < noemotiontopic.size(); ins++) {
					boolean fage = false;
					OWLIndividual owlindi = null;
					for (Iterator is = AllAddPeopleIndividuals.iterator(); is.hasNext();) {
						owlindi = (OWLIndividual) is.next();
						Object ob = owlindi.getPropertyValue(addModelTypeProperty);
						if (ob.toString().equals("people")) {
							if (owlindi.getPropertyValueCount(topicname) != 0) {
								if (owlindi.getPropertyValue(topicname).toString().equals(noemotiontopic.get(ins))) {
									fage = true;
									break;
								}
							}
						}
					}
					if (fage == false) {
						boolean fl = false;
						for (int ins1 = 0; ins1 < emotiontopic.size(); ins1++) {
							for (Iterator is = AllAddPeopleIndividuals.iterator(); is.hasNext();) {
								owlindi = (OWLIndividual) is.next();
								Object ob = owlindi.getPropertyValue(addModelTypeProperty);
								if (ob.toString().equals("people")) {
									if (owlindi.getPropertyValueCount(topicname) != 0) {
										if (owlindi.getPropertyValue(topicname).toString()
												.equals(emotiontopic.get(ins1))) {
											fl = true;
											owlindi.removePropertyValue(topicname, emotiontopic.get(ins1));
											owlindi.setPropertyValue(topicname, noemotiontopic.get(ins));
											// break;
										}
									}
								}
							}
						}

						if (fl == false) {

							count = AddModelRelatedClass.getInstanceCount() + 1;
							String i = String.valueOf(count);
							String str = "addModelID" + i;
							System.out.print("str=" + str + "\t");
							OWLIndividual addModelIndiviual = AddModelRelatedClass.createOWLIndividual(str);
							addModelIndiviual.addPropertyValue(isTemplateObject, "0");
							addModelIndiviual.addPropertyValue(addModelNumber, "1");
							addModelIndiviual.addPropertyValue(modelIDProperty, str);
							addModelIndiviual.addPropertyValue(addModelTypeProperty, "people");
							addModelIndiviual.addPropertyValue(isSuited, "false");
							addModelIndiviual.addPropertyValue(topicname, noemotiontopic.get(ins));
							ArrayList<OWLIndividual> ary = new ArrayList<OWLIndividual>();
							ary.add(model.getOWLIndividual("M_boy.ma"));
							ary.add(model.getOWLIndividual("M_girl.ma"));
							int l = random(ary.size());
							addModelIndiviual.addPropertyValue(hasModelNameProperty, ary.get(l));
							OWLNamedClass co = (OWLNamedClass) ary.get(l).getDirectType();
							String father = co.getBrowserText().toString();
							String spacename = "";
							OWLIndividual ind = model.getOWLIndividual(maName);
							Collection col = ind.getPropertyValues(hasSceneSpace);
							if (col.size() != 0) {
								for (Iterator in = col.iterator(); in.hasNext();) {
									OWLIndividual ols = (OWLIndividual) in.next();
									OWLNamedClass nas = (OWLNamedClass) ols.getDirectType();
									String s = nas.getBrowserText().toString();

									if (s.contains("Ground") && !ols.getBrowserText().toString().contains("Floor")) {
										addModelIndiviual.addPropertyValue(addModelRelatedSpace, ols);
										spacename = ols.getBrowserText().toString();

										break;
									}
								}
							}

							doc = WriteXML1(model, doc, str, ary.get(l).getBrowserText().toString(), father, spacename,
									"people", "1", noemotiontopic.get(ins));
						}
					}
				}
			} else {
				for (int ine = 0; ine < emotiontopic.size(); ine++) {
					boolean fage1 = false;
					for (Iterator is1 = AllAddPeopleIndividuals.iterator(); is1.hasNext();) {
						OWLIndividual owlindi1 = (OWLIndividual) is1.next();
						Object ob1 = owlindi1.getPropertyValue(addModelTypeProperty);
						if (ob1.toString().equals("people")) {
							if (owlindi1.getPropertyValueCount(topicname) != 0) {
								if (owlindi1.getPropertyValue(topicname).toString().equals(emotiontopic.get(ine))) {
									fage1 = true;
									break;
								}
							}

						}
					}
					if (fage1 == false) {
						count = AddModelRelatedClass.getInstanceCount() + 1;
						String ii = String.valueOf(count);
						String stri;
						stri = "addModelID" + ii;
						System.out.println("str=" + stri);
						OWLIndividual addModelIndiviual1 = AddModelRelatedClass.createOWLIndividual(stri);
						addModelIndiviual1.addPropertyValue(isTemplateObject, "0");
						addModelIndiviual1.addPropertyValue(addModelNumber, "1");
						addModelIndiviual1.addPropertyValue(modelIDProperty, stri);
						addModelIndiviual1.addPropertyValue(addModelTypeProperty, "people");
						addModelIndiviual1.addPropertyValue(isSuited, "false");
						addModelIndiviual1.addPropertyValue(topicname, emotiontopic.get(ine));
						ArrayList<OWLIndividual> aryy = new ArrayList<OWLIndividual>();
						aryy.add(model.getOWLIndividual("M_boy.ma"));
						aryy.add(model.getOWLIndividual("M_girl.ma"));
						int m = random(aryy.size());
						addModelIndiviual1.addPropertyValue(hasModelNameProperty, aryy.get(m));
						OWLNamedClass co = (OWLNamedClass) aryy.get(m).getDirectType();
						String father1 = co.getBrowserText().toString();
						String spacename1 = "";
						OWLIndividual ind = model.getOWLIndividual(maName);
						Collection col = ind.getPropertyValues(hasSceneSpace);
						if (col.size() != 0) {
							for (Iterator inq = col.iterator(); inq.hasNext();) {
								OWLIndividual ols = (OWLIndividual) inq.next();
								OWLNamedClass nas = (OWLNamedClass) ols.getDirectType();
								String s = nas.getBrowserText().toString();

								if (s.contains("Ground") && !ols.getBrowserText().toString().contains("Floor")) {
									addModelIndiviual1.addPropertyValue(addModelRelatedSpace, ols);
									spacename1 = ols.getBrowserText().toString();

									break;
								}
							}
						}

						doc = WriteXML1(model, doc, stri, aryy.get(m).getBrowserText().toString(), father1, spacename1,
								"people", "1", emotiontopic.get(ine));
					}

				}
			}

		} else {
			if (noemotiontopic.size() != 0) {
				for (int ine = 0; ine < noemotiontopic.size(); ine++) {
					count = AddModelRelatedClass.getInstanceCount() + 1;
					String ii = String.valueOf(count);
					String stri;
					stri = "addModelID" + ii;
					System.out.println("str=" + stri);
					OWLIndividual addModelIndiviual1 = AddModelRelatedClass.createOWLIndividual(stri);
					addModelIndiviual1.addPropertyValue(isTemplateObject, "0");
					addModelIndiviual1.addPropertyValue(addModelNumber, "1");
					addModelIndiviual1.addPropertyValue(modelIDProperty, stri);
					addModelIndiviual1.addPropertyValue(addModelTypeProperty, "people");
					addModelIndiviual1.addPropertyValue(isSuited, "false");
					addModelIndiviual1.addPropertyValue(topicname, noemotiontopic.get(ine));
					ArrayList<OWLIndividual> aryy = new ArrayList<OWLIndividual>();
					aryy.add(model.getOWLIndividual("M_boy.ma"));
					aryy.add(model.getOWLIndividual("M_girl.ma"));
					int m = random(aryy.size());
					addModelIndiviual1.addPropertyValue(hasModelNameProperty, aryy.get(m));
					OWLNamedClass co = (OWLNamedClass) aryy.get(m).getDirectType();
					String father1 = co.getBrowserText().toString();
					String spacename1 = "";
					OWLIndividual ind = model.getOWLIndividual(maName);
					Collection col = ind.getPropertyValues(hasSceneSpace);
					if (col.size() != 0) {
						for (Iterator inq = col.iterator(); inq.hasNext();) {
							OWLIndividual ols = (OWLIndividual) inq.next();
							OWLNamedClass nas = (OWLNamedClass) ols.getDirectType();
							String s = nas.getBrowserText().toString();

							if (s.contains("Ground") && !ols.getBrowserText().toString().contains("Floor")) {
								addModelIndiviual1.addPropertyValue(addModelRelatedSpace, ols);
								spacename1 = ols.getBrowserText().toString();

								break;
							}
						}
					}

					doc = WriteXML1(model, doc, stri, aryy.get(m).getBrowserText().toString(), father1, spacename1,
							"people", "1", noemotiontopic.get(ine));
				}
			}

			else if (emotiontopic.size() != 0) {
				for (int ine = 0; ine < emotiontopic.size(); ine++) {
					count = AddModelRelatedClass.getInstanceCount() + 1;
					String ii = String.valueOf(count);
					String stri;
					stri = "addModelID" + ii;
					System.out.println("str=" + stri);
					OWLIndividual addModelIndiviual1 = AddModelRelatedClass.createOWLIndividual(stri);
					addModelIndiviual1.addPropertyValue(isTemplateObject, "0");
					addModelIndiviual1.addPropertyValue(addModelNumber, "1");
					addModelIndiviual1.addPropertyValue(modelIDProperty, stri);
					addModelIndiviual1.addPropertyValue(addModelTypeProperty, "people");
					addModelIndiviual1.addPropertyValue(isSuited, "false");
					addModelIndiviual1.addPropertyValue(topicname, emotiontopic.get(ine));
					ArrayList<OWLIndividual> aryy = new ArrayList<OWLIndividual>();
					aryy.add(model.getOWLIndividual("M_boy.ma"));
					aryy.add(model.getOWLIndividual("M_girl.ma"));
					int m = random(aryy.size());
					addModelIndiviual1.addPropertyValue(hasModelNameProperty, aryy.get(m));
					OWLNamedClass co = (OWLNamedClass) aryy.get(m).getDirectType();
					String father1 = co.getBrowserText().toString();
					String spacename1 = "";
					OWLIndividual ind = model.getOWLIndividual(maName);
					Collection col = ind.getPropertyValues(hasSceneSpace);
					if (col.size() != 0) {
						for (Iterator inq = col.iterator(); inq.hasNext();) {
							OWLIndividual ols = (OWLIndividual) inq.next();
							OWLNamedClass nas = (OWLNamedClass) ols.getDirectType();
							String s = nas.getBrowserText().toString();

							if (s.contains("Ground") && !ols.getBrowserText().toString().contains("Floor")) {
								addModelIndiviual1.addPropertyValue(addModelRelatedSpace, ols);
								spacename1 = ols.getBrowserText().toString();

								break;
							}
						}
					}

					doc = WriteXML1(model, doc, stri, aryy.get(m).getBrowserText().toString(), father1, spacename1,
							"people", "1", emotiontopic.get(ine));
				}
			}

		}
		return doc;
	}

	private static ArrayList<OWLNamedClass> getEvent1(String noemotiontopic1, OWLObjectProperty eventop,
			OWLModel owlModel) {
		// TODO Auto-generated method stub

		ArrayList<OWLNamedClass> NoEmotionEvent1 = new ArrayList<OWLNamedClass>();
		ArrayList<String> topicclass = new ArrayList<String>();
		ArrayList<String> emotionclass = new ArrayList<String>();
		String str1 = null;
		// ArrayList<OWLNamedClass> emotionclasssum=new
		// ArrayList<OWLNamedClass>(emotiontopic.size());
		OWLNamedClass eventclass = owlModel.getOWLNamedClass(str + "Event");
		// OWLObjectProperty
		// eventop=owlModel.getOWLObjectProperty(str+"hasSuitTopic");
		Collection clo = eventclass.getSubclasses(true);
		OWLNamedClass evt = null;
		for (Iterator is = clo.iterator(); is.hasNext();) {
			evt = (OWLNamedClass) is.next();
			// 获取事件对应的主题
			topicclass.clear();
			RDFResource resource = evt.getSomeValuesFrom(eventop);
			if (resource != null) {
				String hasValues = resource.getBrowserText();
				if (hasValues.indexOf("or") >= 0) {
					String[] hasValuesSplit = hasValues.split("or");
					if (hasValuesSplit.length > 0) {// 判断是否对应多个主题
						for (int i = 0; i < hasValuesSplit.length; i++) {
							topicclass.add(hasValuesSplit[i].toString().trim());
						}
					}
				} else {
					topicclass.add(resource.getBrowserText());
				}
			}

			for (Iterator ite = topicclass.iterator(); ite.hasNext();) {
				String ols = (String) ite.next();
				if (ols.equals(noemotiontopic1)) {
					NoEmotionEvent1.add(evt);
					NoEmotionEvent.add(evt);
				}

			}

		}
		System.out.println("主题对应的noEmotionEvent=" + NoEmotionEvent1);
		/*
		 * if(NoEmotionEvent1.size()==0) flage.add("false");//判断是否还有主题
		 * flage=false表示没有与之对应的事件 else flage.add("true");
		 */
		return NoEmotionEvent1;

	}

	public boolean ifContainEvent() {
		boolean flag = false;
		if (flage.size() != 0) {

			for (int i = 0; i < flage.size(); i++) {
				System.out.println(flage.get(i));
				if (flage.get(i).equals("true")) {
					flag = true;
					break;
				}
			}
		}
		if (flag == false && NoLinkEvent == true)
			flag = false;

		return flag;

	}

	// 得到与主题相关的事件类
	public static ArrayList<OWLNamedClass> getEvent(ArrayList<String> emotiontopic, OWLObjectProperty eventop,
			OWLModel owlModel) {
		ArrayList<OWLNamedClass> emotione = new ArrayList<OWLNamedClass>();
		ArrayList<String> topicclass = new ArrayList<String>();
		ArrayList<String> emotionclass = new ArrayList<String>();
		String str1 = null;
		ArrayList<OWLNamedClass> emotionclasssum = new ArrayList<OWLNamedClass>(emotiontopic.size());
		OWLNamedClass eventclass = owlModel.getOWLNamedClass(str + "Event");

		Collection clo = eventclass.getSubclasses(true);
		OWLNamedClass evt = null;
		for (Iterator is = clo.iterator(); is.hasNext();) {
			evt = (OWLNamedClass) is.next();
			// 获取事件对应的主题
			topicclass.clear();
			RDFResource resource = evt.getSomeValuesFrom(eventop);
			if (resource != null) {
				String hasValues = resource.getBrowserText();
				if (hasValues.indexOf("or") >= 0) {
					String[] hasValuesSplit = hasValues.split("or");
					if (hasValuesSplit.length > 0) {// 判断是否对应多个主题
						for (int i = 0; i < hasValuesSplit.length; i++) {
							topicclass.add(hasValuesSplit[i].toString().trim());
						}
					}
				} else {
					topicclass.add(resource.getBrowserText());
				}
			}

			for (Iterator ite = topicclass.iterator(); ite.hasNext();) {
				String ols = (String) ite.next();
				for (int it = 0; it < emotiontopic.size(); it++) {
					if (ols.equals(emotiontopic.get(it))) {
						str1 = evt.getBrowserText().toString() + "-" + emotiontopic.get(it);
						emotionclass.add(str1);
						emotione.add(evt);
					}
				}

			}

		}
		if (emotiontopic.size() > 1)
			emotione = isOppositeofSameTopic(owlModel, emotionclass, emotiontopic);// 判断是否含有相同的元素
		System.out.println("EmotionEvent=" + emotione);// 获得的是与主题对应的类名
		/*
		 * if(emotione.size()==0) flage.add("false"); else flage.add("true");
		 */
		return emotione;
	}

	public static ArrayList<OWLNamedClass> getEventFromTemplate(ArrayList<String> actiontemplate,
			OWLObjectProperty eventop, OWLModel owlModel) {
		ArrayList<OWLNamedClass> emotione = new ArrayList<OWLNamedClass>();
		ArrayList<String> topicclass = new ArrayList<String>();
		ArrayList<String> emotionclass = new ArrayList<String>();

		ArrayList<OWLNamedClass> emotionclasssum = new ArrayList<OWLNamedClass>();
		OWLNamedClass eventclass = owlModel.getOWLNamedClass(str + "Event");

		Collection clo = eventclass.getInstances(true);
		OWLIndividual evt = null;

		for (int k = 0; k < actiontemplate.size(); k++) {
			String str = actiontemplate.get(k);
			String str1 = str.substring(0, str.indexOf(":"));
			String str2 = str.substring(str.indexOf(":") + 1);
			Collection coll = owlModel.getOWLNamedClass("MoodTemplate").getSubclasses();
			loop: for (Iterator er = coll.iterator(); er.hasNext();) {
				OWLNamedClass subercla = (OWLNamedClass) er.next();
				if (subercla.getBrowserText().equals(str1)) {
					for (Iterator is = clo.iterator(); is.hasNext();) {
						evt = (OWLIndividual) is.next();
						if (evt.getPropertyValueCount(eventop) != 0) {
							Collection cllo = evt.getPropertyValues(eventop);
							for (Iterator it = cllo.iterator(); it.hasNext();) {
								OWLIndividual ind = (OWLIndividual) it.next();
								if (ind.getBrowserText().toString().equals(str2)) {
									OWLNamedClass type = (OWLNamedClass) evt.getDirectType();
									emotione.add(type);
								}
							}

						}
					}
					break loop;
				}
			}
		}

		System.out.println("EmotionEvent=" + emotione);// 获得的是与模板对应的类名
		/*
		 * if(emotione.size()==0) flage.add("false"); else flage.add("true");
		 */
		return emotione;
	}

	public static ArrayList<OWLNamedClass> getActionEvent(ArrayList<String> actiontemp, OWLObjectProperty eventop,
			OWLModel owlModel) {
		ArrayList<OWLNamedClass> actionone = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> templateclass = new ArrayList<OWLNamedClass>();

		OWLNamedClass eventclass = owlModel.getOWLNamedClass(str + "Event");

		Collection clo = eventclass.getInstances(true);
		OWLIndividual evt = null;
		for (int k = 0; k < actiontemp.size(); k++) {
			for (Iterator is = clo.iterator(); is.hasNext();) {
				evt = (OWLIndividual) is.next();
				if (evt.getPropertyValueCount(eventop) != 0) {
					Collection cllo = evt.getPropertyValues(eventop);
					for (Iterator it = cllo.iterator(); it.hasNext();) {
						OWLIndividual ind = (OWLIndividual) it.next();
						if (ind.getBrowserText().toString().equals(actiontemp.get(k))) {
							OWLNamedClass type = (OWLNamedClass) evt.getDirectType();
							if (type != null)
								templateclass.add(type);
						}
					}

				}

			}
		}
		if (templateclass.size() != 0) {
			if (templateclass.size() > 1) {
				int kk = random(templateclass.size());
				actionone.add(templateclass.get(kk));
			} else {
				if (actionone.size() == 0)
					actionone.add(templateclass.get(0));
				else {
					boolean flage2 = true;
					for (int ii = 0; ii < actionone.size(); ii++) {
						if (actionone.get(ii).equals(templateclass.get(0))) {
							flage2 = false;
							break;
						}
					}
					if (flage2 == true) {
						actionone.add(templateclass.get(0));
					}
				}
			}
		}

		System.out.println("actionEvent=" + actionone);// 获得的是与主题对应的类名
		/*
		 * if(actionone.size()==1) { flage.add("false"); NoLinkEvent=false; }
		 * 
		 * else flage.add("true"); System.out.println(flage);
		 */
		return actionone;
	}

	public static ArrayList<OWLNamedClass> isOppositeofSameTopic(OWLModel model, ArrayList<String> emotionclass,
			ArrayList<String> emotiontopic) {
		ArrayList<OWLNamedClass> emotions = new ArrayList<OWLNamedClass>();
		String st = null;
		String sr = null;
		String s = null;

		int n = 0;
		int a = 0;
		int k = 0;
		for (k = 0; k < emotionclass.size() - 1; k++) {
			st = emotionclass.get(k);
			for (int m = k + 1; m < emotionclass.size(); m++) {
				sr = emotionclass.get(m);
				if (sr.substring(sr.indexOf("-") + 1).equals(st.substring(st.indexOf("-") + 1))) {
					n = random(2);
					if (n == 0)
						s = st.substring(0, st.indexOf("-"));
					else
						s = sr.substring(0, sr.indexOf("-"));
					emotionclass.remove(m);
				}

			}
		}
		emotions.clear();
		for (int ins = 0; ins < emotiontopic.size(); ins++) {
			for (k = 0; k < emotionclass.size(); k++) {
				st = emotionclass.get(k);
				if (st.substring(st.indexOf("-") + 1).equals(emotiontopic.get(ins))) {
					OWLNamedClass cs = model.getOWLNamedClass(st.substring(0, st.indexOf("-")));
					emotions.add(cs);
				}

			}

		}
		return emotions;
	}

	// 判断添加的模型中是否含有人物
	public static Document getPeople(OWLModel model, String maName, String noemotiontopic1,
			ArrayList<OWLNamedClass> noEmotionEvent, boolean f, Document doc) {
		System.out.println("是否是非情感主题事件：" + f);// f用来判断是否含有情感主题或事件
		System.out.println("当前选择的事件是：" + noEmotionEvent);
		ArrayList<OWLIndividual> EventIndividual = new ArrayList<OWLIndividual>();
		int count = 0;
		boolean flag = false;
		OWLNamedClass AddModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty addModelNumber = model.getOWLDatatypeProperty("addModelNumber");
		OWLDatatypeProperty topicname = model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty isTemplateObject = model.getOWLDatatypeProperty("isTemplateObject");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty addModelRelatedSpace = model.getOWLObjectProperty("addModelRelatedSpace");
		OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
		OWLObjectProperty hasSubject = model.getOWLObjectProperty(str + "hasSubject");
		OWLDatatypeProperty SubjectNumber = model.getOWLDatatypeProperty(str + "SubjectNumber");
		OWLDatatypeProperty isDeal = model.getOWLDatatypeProperty("isUsed");
		Collection AllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		OWLIndividual addModelIndiviual = null;
		OWLIndividual ins = null;
		String addmodlins = "";
		String addmodlin = "";
		String addmodlIsDeal = "";
		int a = 0;

		for (Iterator it = AllAddPeopleIndividuals.iterator(); it.hasNext();) {
			addModelIndiviual = (OWLIndividual) it.next();
			if (addModelIndiviual.getPropertyValueCount(addModelTypeProperty) != 0)
				addmodlins = addModelIndiviual.getPropertyValue(addModelTypeProperty).toString();
			if (addModelIndiviual.getPropertyValueCount(topicname) != 0)
				addmodlin = addModelIndiviual.getPropertyValue(topicname).toString();
			if (addModelIndiviual.getPropertyValueCount(isDeal) != 0) {

				addmodlIsDeal = addModelIndiviual.getPropertyValue(isDeal).toString();

			}
			if (addmodlins.equals("people") && noemotiontopic1.equals(addmodlin) && addmodlIsDeal.equals("false")) {// 添加的模型的主题与该主题一致且是人物

				System.out.println(addModelIndiviual.getBrowserText() + "\t" + noemotiontopic1);

				String PeopleID = addModelIndiviual.getPropertyValue(modelIDProperty).toString();
				String PeopleNameInOwl = ((OWLIndividual) addModelIndiviual.getPropertyValue(hasModelNameProperty))
						.getBrowserText();
				int pos = PeopleNameInOwl.indexOf(":");
				String PeopleName = (String) PeopleNameInOwl.subSequence(pos + 1, PeopleNameInOwl.length());
				System.out.println("a=" + (++a));
				if (f == true)
					EventIndividual = getLinkEvent(EmotionEvent, noEmotionEvent, model, doc);
				else if (f == false) {
					EventIndividual = getLinkEvent1(model, doc, noEmotionEvent);// 情感事件是个全局变量
				}
				if (EventIndividual != null) {
					flag = true;
					for (Iterator ite = EventIndividual.iterator(); ite.hasNext();) {
						ins = (OWLIndividual) ite.next();
						ins.setPropertyValue(SubjectNumber, PeopleID);
						ins.setPropertyValue(hasSubject, addModelIndiviual.getPropertyValue(hasModelNameProperty));
					}
					// 标志位，代表该人物已经经过动作所处理
					addModelIndiviual.setPropertyValue(isDeal, "true");
					doc = print(EventIndividual, model, doc);
				} else {

					System.out.println(addModelIndiviual.getBrowserText() + "\t" + addmodlin);
					flage.add("false");
				}

			} else if (addmodlins.equals("people") && addmodlIsDeal.equals("false")) {

				System.out.println(addModelIndiviual.getBrowserText() + "\t" + addmodlin);
				flage.add("false");
			}

		}

		return doc;

	}

	public static Document getPeopleToTemplate(OWLModel model, String maName, ArrayList<OWLNamedClass> actionEvent,
			Document doc) {

		System.out.println("当前选择的事件是：" + actionEvent);
		ArrayList<OWLIndividual> EventIndividual = new ArrayList<OWLIndividual>();
		int count = 0;
		boolean flag = false;
		OWLNamedClass AddModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty addModelNumber = model.getOWLDatatypeProperty("addModelNumber");
		OWLDatatypeProperty topicname = model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty isTemplateObject = model.getOWLDatatypeProperty("isTemplateObject");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty addModelRelatedSpace = model.getOWLObjectProperty("addModelRelatedSpace");
		OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
		OWLObjectProperty hasSubject = model.getOWLObjectProperty(str + "hasSubject");
		OWLDatatypeProperty SubjectNumber = model.getOWLDatatypeProperty(str + "SubjectNumber");
		OWLDatatypeProperty isSuited = model.getOWLDatatypeProperty("isUsed");

		Collection AllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		OWLIndividual addModelIndiviual = null;
		OWLIndividual ins = null;
		String addmodlins = "";
		String addmodlin = "";
		// 20170530
		String addmodeIsUsed = "";
		int a = 0;
		for (Iterator it = AllAddPeopleIndividuals.iterator(); it.hasNext();) {
			addModelIndiviual = (OWLIndividual) it.next();
			if (addModelIndiviual.getPropertyValueCount(addModelTypeProperty) != 0)
				addmodlins = addModelIndiviual.getPropertyValue(addModelTypeProperty).toString();
			if (addModelIndiviual.getPropertyValueCount(topicname) != 0)
				addmodlin = addModelIndiviual.getPropertyValue(topicname).toString();
			// 20170530
			if (addmodlins.equals("people")) {
				if (addModelIndiviual.getPropertyValueCount(isSuited) != 0) {
					addmodeIsUsed = addModelIndiviual.getPropertyValue(isSuited).toString();
				}
				System.out.println("addmodeIsUsed=" + addmodeIsUsed);
			}

			// 0530

			if (addmodlins.equals("people") && addmodeIsUsed.equals("false")) {// 添加的模型的主题与该主题一直且是人物
																				// if(addModelIndiviual.getPropertyValueCount(isSuited)==0)
																				// {
				System.out.println("people ID=" + addModelIndiviual.getBrowserText());
				flag = true;
				String PeopleID = addModelIndiviual.getPropertyValue(modelIDProperty).toString();
				String PeopleNameInOwl = ((OWLIndividual) addModelIndiviual.getPropertyValue(hasModelNameProperty))
						.getBrowserText();
				int pos = PeopleNameInOwl.indexOf(":");
				String PeopleName = (String) PeopleNameInOwl.subSequence(pos + 1, PeopleNameInOwl.length());
				System.out.println("第" + (++a) + "个人");

				EventIndividual = getLinkEvent(EmotionEvent, actionEvent, model, doc);// 情感事件是个全局变量

				if (EventIndividual != null) {
					for (Iterator ite = EventIndividual.iterator(); ite.hasNext();) {
						ins = (OWLIndividual) ite.next();
						ins.setPropertyValue(SubjectNumber, PeopleID);
						ins.setPropertyValue(hasSubject, addModelIndiviual.getPropertyValue(hasModelNameProperty));
					}
					addModelIndiviual.addPropertyValue(isSuited, "true");
					doc = print(EventIndividual, model, doc);

					flage.add("true");
				} else {
					flage.add("false");
					NoLinkEvent = true;
					System.out.println("没有相关动作");
				}
				// }

			}

		}

		return doc;

	}

	// 将选择好的动作写入xml中
	public static Document print(ArrayList<OWLIndividual> EventIndividual, OWLModel model, Document doc) {
		OWLIndividual ind = null;
		OWLObjectProperty hasSubject = model.getOWLObjectProperty(str + "hasSubject");
		OWLDatatypeProperty SubjectNumber = model.getOWLDatatypeProperty(str + "SubjectNumber");
		OWLObjectProperty hasReference = model.getOWLObjectProperty(str + "hasReference");
		OWLObjectProperty hasDistence = model.getOWLObjectProperty(str + "hasDistence");
		OWLObjectProperty hasSuitAction = model.getOWLObjectProperty(str + "hasSuitAction");
		OWLDatatypeProperty ifAddConstraintProperty = model.getOWLDatatypeProperty("p2:ifAddConstraint");
		for (Iterator it = EventIndividual.iterator(); it.hasNext();) {
			ind = (OWLIndividual) it.next();
			// System.out.println(ind.getPropertyValue(hasSubject));
			OWLIndividual indi = (OWLIndividual) ind.getPropertyValue(hasSubject);
			String people = indi.getBrowserText().toString();
			String id = ind.getPropertyValue(SubjectNumber).toString();
			String event = ind.getDirectType().getBrowserText().toString().substring(3);
			String distence = ((OWLIndividual) ind.getPropertyValue(hasDistence)).getBrowserText().toString();
			String s = ind.getPropertyValue(ifAddConstraintProperty).toString();
			distence = distence.substring(3);
			// OWLIndividual
			// SL=(OWLIndividual)ind.getPropertyValue(hasReference);

			String reference = "";
			/*
			 * if(SL!=null) reference=SL.getBrowserText().toString(); else
			 * reference="";
			 */
			String action = ((OWLIndividual) ind.getPropertyValue(hasSuitAction)).getBrowserText().toString();
			action = action.substring(3);
			System.out.println(people + "\t" + id + "\t" + distence + "\t" + reference + "\t" + action + "\t" + s);
			doc = WriteXML(model, doc, event, id, people, action, distence, reference, s);
		}

		return doc;
	}

	// 读取xml
	public static Document WriteXML(OWLModel model, Document doc, String event, String id, String people, String action,
			String distence, String reference, String f) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addActionToMa");// type="model"
		ruleName.addAttribute("type", "action");
		ruleName.addAttribute("eventType", event);
		ruleName.addAttribute("usedModelID", id);
		ruleName.addAttribute("usedModelInMa", people);
		ruleName.addAttribute("actionName", action);
		ruleName.addAttribute("distance", distence);
		ruleName.addAttribute("reference", reference);
		ruleName.addAttribute("ifAddConstraint", f);
		return doc;
	}

	public static Document WriteXML1(OWLModel model, Document doc, String id, String maname, String father,
			String spacename, String type, String number, String topicname) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");

		ruleName.addAttribute("ruleType", "addToMa");// type="model"
		ruleName.addAttribute("addModel", maname);
		ruleName.addAttribute("class", father);
		ruleName.addAttribute("spaceName", spacename);
		ruleName.addAttribute("type", type);
		ruleName.addAttribute("isTarget", "1");
		ruleName.addAttribute("degree", "");
		ruleName.addAttribute("number", number);
		ruleName.addAttribute("addModelID", id);
		System.out.println("添加模型成功");
		return doc;
	}

	// 获得与主事件相关的事件
	public static ArrayList<OWLIndividual> getLinkEvent(ArrayList<OWLNamedClass> EmotionEvent,
			ArrayList<OWLNamedClass> NoEmotionEvent, OWLModel model, Document doc) {
		System.out.println("获得与主事件相关的事件");

		ArrayList<OWLNamedClass> Event = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> PreEvent = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> NextEvent = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> ResultEvent = new ArrayList<OWLNamedClass>();
		OWLNamedClass MainEvent = null;
		OWLNamedClass event = null;
		int m = 0;
		int k = 0;
		if (NoEmotionEvent.size() != 0) {
			if (NoEmotionEvent.size() > 1) {
				int ran = random(NoEmotionEvent.size());
				MainEvent = NoEmotionEvent.get(ran);
			} else if (NoEmotionEvent.size() == 1)
				MainEvent = NoEmotionEvent.get(0);
			System.out.println("MainEvent=" + MainEvent);
			if (EmotionEvent.size() != 0) {
				k = random(EmotionEvent.size());
				Event.add(MainEvent);
				Event.add(EmotionEvent.get(k));
			} else {
				PreEvent = getEvent2(MainEvent, model, "hasPreEvent");
				NextEvent = getEvent2(MainEvent, model, "hasNextEvent");
				ResultEvent = getEvent2(MainEvent, model, "hasResult");

				k = random(3);

				switch (k) {
				case 0:
					if (PreEvent.size() != 0) {
						m = random(PreEvent.size());
						Event.add(PreEvent.get(m));
						Event.add(MainEvent);
					} else {
						if (NextEvent.size() != 0) {
							Event.add(MainEvent);
							m = random(NextEvent.size());
							Event.add(NextEvent.get(m));
						} else
							Event.add(MainEvent);
					}
					break;
				case 1:
					if (NextEvent.size() != 0) {
						Event.add(MainEvent);
						m = random(NextEvent.size());
						Event.add(NextEvent.get(m));
					} else {
						if (PreEvent.size() != 0) {
							m = random(PreEvent.size());
							Event.add(PreEvent.get(m));
							Event.add(MainEvent);
						} else
							Event.add(MainEvent);
					}
					break;
				case 2:
					Event.add(MainEvent);
					break;
				}

				// 如果EmotionEvent存在，在添加事件，否则如果事件中只含有一个主事件，那么从Result中添加一个事件
				if (Event.size() > 1) {
					if (EmotionEvent.size() != 0) {
						m = random(EmotionEvent.size());
						Event.add(EmotionEvent.get(m));
					}
				} else if (Event.size() == 1) {

					if (EmotionEvent.size() != 0) {
						m = random(EmotionEvent.size());
						Event.add(EmotionEvent.get(m));
					} else {
						if (ResultEvent.size() != 0) {
							m = random(ResultEvent.size());
							Event.add(ResultEvent.get(m));
						} else {
							if (PreEvent.size() != 0) {
								Event.clear();
								m = random(PreEvent.size());
								Event.add(PreEvent.get(m));
								Event.add(MainEvent);
							} else {
								if (NextEvent.size() != 0) {
									Event.clear();
									Event.add(MainEvent);
									m = random(NextEvent.size());
									Event.add(NextEvent.get(m));
								}
							}
						}
					}
				}
			}
		}

		System.out.println("MainEvent=" + MainEvent);
		System.out.println("Event=" + Event);
		System.out.println("---------------");
		ArrayList<OWLIndividual> EventIndividual = new ArrayList<OWLIndividual>();
		if (Event.size() > 1) {
			EventIndividual = getIndividual(MainEvent, Event, model, doc);
			flage.add("true");
			return EventIndividual;
		} else {
			NoLinkEvent = true;

			return null;
		}

	}

	public static ArrayList<OWLIndividual> getLinkEvent1(OWLModel model, Document doc,
			ArrayList<OWLNamedClass> emotionEvent) {
		System.out.println("获得与情绪事件相关的主事件");
		ArrayList<OWLNamedClass> Event = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> PreEvent = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> NextEvent = new ArrayList<OWLNamedClass>();
		ArrayList<OWLNamedClass> ResultEvent = new ArrayList<OWLNamedClass>();
		OWLNamedClass MainEvent = null;
		OWLNamedClass event = null;
		int m = 0;
		if (emotionEvent.size() != 0) {
			if (emotionEvent.size() > 1) {// 如果情绪事件个数大于一，则从最后一个情绪事件中选择它的effect事件，这样组成多个事件组合
				MainEvent = emotionEvent.get(emotionEvent.size() - 1);
				PreEvent = getEvent2(MainEvent, model, "hasEffect");
				// Event.add(emotionEvent.get(0));
				if (PreEvent.size() != 0) {
					m = random(PreEvent.size());
					Event.add(PreEvent.get(m));
					Event.add(MainEvent);
					MainEvent = PreEvent.get(m);
				} else {
					Event.add(MainEvent);
					// MainEvent=null;
				}
			} else if (emotionEvent.size() == 1) {// 如果情绪事件只有一个，那么就可以选择它的原因事件或者是后继事件
				MainEvent = emotionEvent.get(0);
				PreEvent = getEvent2(MainEvent, model, "hasEffect");
				NextEvent = getEvent2(MainEvent, model, "hasNextEvent");
				int k = random(2);

				switch (k) {
				case 0:
					if (PreEvent.size() != 0) {
						m = random(PreEvent.size());
						Event.add(PreEvent.get(m));
						Event.add(MainEvent);
						MainEvent = PreEvent.get(m);
					} else {
						if (NextEvent.size() != 0) {
							Event.add(MainEvent);
							m = random(NextEvent.size());
							Event.add(NextEvent.get(m));
							MainEvent = NextEvent.get(m);
						}
					}
					break;
				case 1:
					if (NextEvent.size() != 0) {
						Event.add(MainEvent);
						m = random(NextEvent.size());
						Event.add(NextEvent.get(m));
						MainEvent = NextEvent.get(m);
					} else {
						if (PreEvent.size() != 0) {
							m = random(PreEvent.size());
							Event.add(PreEvent.get(m));
							Event.add(MainEvent);
							MainEvent = PreEvent.get(m);
						}
					}
					break;
				}
			}
		}
		System.out.println("MainEvent=" + MainEvent);
		System.out.println("Event=" + Event);
		System.out.println("---------------");
		ArrayList<OWLIndividual> EventIndividual = new ArrayList<OWLIndividual>();
		if (Event.size() > 1) {
			EventIndividual = getIndividual(MainEvent, Event, model, doc);
			flage.add("true");
			return EventIndividual;
		} else {
			NoLinkEvent = true;
			System.out.println("没有关联事件");
			return null;
		}

	}

	public static ArrayList<OWLIndividual> getIndividual(OWLNamedClass mainEvent, ArrayList<OWLNamedClass> Event,
			OWLModel model, Document doc)// 获取事件实例，并添加动作和参照物
	{

		System.out.println("调用实例");
		ArrayList<OWLIndividual> EventIndividual = new ArrayList<OWLIndividual>();
		OWLNamedClass evnt = null;
		OWLIndividual owlsd = null;
		OWLDatatypeProperty isFirstPlay = model.getOWLDatatypeProperty(str + "ifFirstPlay");
		OWLObjectProperty hasDistence = model.getOWLObjectProperty(str + "hasDistence");
		OWLObjectProperty hasReference = model.getOWLObjectProperty(str + "hasReference");
		OWLObjectProperty hasSuitAction = model.getOWLObjectProperty(str + "hasSuitAction");
		OWLObjectProperty hasSuitTopic = model.getOWLObjectProperty(str + "hasSuitTopic");
		OWLDatatypeProperty ifAddConstraintProperty = model.getOWLDatatypeProperty("p2:ifAddConstraint");
		OWLNamedClass AddModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty topicname = model.getOWLDatatypeProperty("topicName");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty addModelRelatedSpace = model.getOWLObjectProperty("addModelRelatedSpace");

		OWLIndividual place = null;
		OWLIndividual reference = null;
		ArrayList<OWLIndividual> insc = new ArrayList<OWLIndividual>();
		ArrayList<String> actionframe = new ArrayList();
		for (int i = 0; i < Event.size(); i++) {
			if (Event.get(i).getBrowserText().toString().equals(mainEvent.getBrowserText().toString())) {
				Collection col = mainEvent.getInstances();
				if (col.size() != 0) {
					for (Iterator it = col.iterator(); it.hasNext();) {
						owlsd = (OWLIndividual) it.next();
						insc.add(owlsd);
					}
					int k = random(insc.size());
					owlsd = insc.get(k);
					reference = (OWLIndividual) owlsd.getPropertyValue(hasReference);
				}

			}
		}
		if (reference != null) {
			reference = getSlibIndividual(reference, model);
			doc = isContainReference(reference, name, model, doc);
		}
		/*
		 * else{ for(int j=0;j<Event.size();j++){ String
		 * event=Event.get(j).getBrowserText().toString();
		 * if(event.equals(str+"WalkEvent") || event.equals(str+"RunEvent")){
		 * ArrayList addModel=new ArrayList();//存放于主要事件对应的主题 RDFResource topic=
		 * mainEvent.getSomeValuesFrom(hasSuitTopic); if(topic!=null) { String
		 * topicstring=topic.getBrowserText(); if(topicstring.contains("or")) {
		 * String[] hasvalue=topicstring.split("or"); for(int
		 * m=0;m<hasvalue.length;m++) { addModel.add(hasvalue[m]); } } else
		 * addModel.add(topicstring); } Collection
		 * clos=AddModelRelatedClass.getInstances(); ArrayList<OWLIndividual>
		 * ground=new ArrayList<OWLIndividual>(); loop:for(Iterator
		 * is=clos.iterator();is.hasNext();) { OWLIndividual
		 * owls=(OWLIndividual) is.next(); OWLIndividual spac=(OWLIndividual)
		 * owls.getPropertyValue(addModelRelatedSpace); if(spac!=null) {
		 * 
		 * OWLNamedClass spacePlace=(OWLNamedClass) spac.getDirectType(); String
		 * topicli=(String) owls.getPropertyValue(topicname);
		 * 
		 * for(int k=0;k<addModel.size();k++) { String
		 * s=addModel.get(k).toString(); if(s.equals(topicli)) {
		 * if(spacePlace.getBrowserText().contains("Ground") &&
		 * owls.getPropertyValue(addModelTypeProperty).equals("model") &&
		 * !owls.getPropertyValue(hasModelNameProperty).equals("M_floor.ma"))
		 * reference=(OWLIndividual)
		 * owls.getPropertyValue(hasModelNameProperty); break loop; } }
		 * if(spacePlace.getBrowserText().contains("Ground") &&
		 * owls.getPropertyValue(addModelTypeProperty).equals("model") &&
		 * !owls.getPropertyValue(hasModelNameProperty).equals("M_floor.ma"))
		 * ground.add(owls); }
		 * 
		 * } if(ground.size()!=0 && reference==null) { int
		 * k=random(ground.size()); OWLIndividual re=ground.get(k);
		 * reference=(OWLIndividual) re.getPropertyValue(hasModelNameProperty);
		 * } if(reference==null) { reference=setReference(model);
		 * System.out.println("reference="+reference);
		 * doc=isContainReference(reference,name,model,doc); } } } }
		 */
		place = (OWLIndividual) model.getOWLIndividual(str + "Default");
		System.out.println("reference=" + reference);

		int sumframe = 0;
		int frame = 0;
		OWLNamedClass nac = null;
		OWLIndividual indf = null;
		OWLIndividual act = null;
		String s = "";
		ArrayList<OWLIndividual> insum = new ArrayList<OWLIndividual>();
		int t = 1;
		for (Iterator i = Event.iterator(); i.hasNext();) {

			nac = (OWLNamedClass) i.next();
			System.out.println("event" + t + "=" + nac);
			t++;
			if (!nac.equals(mainEvent)) {
				Collection clo = nac.getInstances();
				if (clo.size() != 0) {
					for (Iterator ins = clo.iterator(); ins.hasNext();)
						insum.add((OWLIndividual) ins.next());
					int k = random(insum.size());
					indf = insum.get(k);
					indf.setPropertyValue(hasReference, reference);
					indf.setPropertyValue(hasDistence, place);
					act = getAction(nac, model);
					frame = getFrame(model, act);
					s = getifAddConstraint(act, model);
					indf.setPropertyValue(hasSuitAction, act);
					indf.setPropertyValue(ifAddConstraintProperty, s);
					EventIndividual.add(indf);
				} else {
					indf = nac.createOWLIndividual(str + nac.getBrowserText().toString() + "1");
					indf.setPropertyValue(hasReference, reference);
					indf.setPropertyValue(hasDistence, place);
					act = getAction(nac, model);
					frame = getFrame(model, act);
					s = getifAddConstraint(act, model);
					indf.setPropertyValue(hasSuitAction, act);
					indf.setPropertyValue(ifAddConstraintProperty, s);
					EventIndividual.add(indf);
				}
			} else if (nac.equals(mainEvent)) {
				act = getAction(mainEvent, model);
				frame = getFrame(model, act);
				if (owlsd != null) {
					owlsd.setPropertyValue(hasSuitAction, act);
					s = getifAddConstraint(act, model);
					owlsd.setPropertyValue(ifAddConstraintProperty, s);
					owlsd.setPropertyValue(hasReference, reference);
					if (owlsd.getPropertyValue(hasDistence) == null)
						owlsd.setPropertyValue(hasDistence, place);
					EventIndividual.add(owlsd);
				} else {
					indf = nac.createOWLIndividual(str + nac.getBrowserText().toString() + "1");
					indf.setPropertyValue(hasReference, reference);
					indf.setPropertyValue(hasDistence, place);
					act = getAction(nac, model);
					frame = getFrame(model, act);
					s = getifAddConstraint(act, model);
					indf.setPropertyValue(hasSuitAction, act);
					indf.setPropertyValue(ifAddConstraintProperty, s);
					EventIndividual.add(indf);
				}
			}

			System.out.println("EventIndividual=" + EventIndividual);
			if (frame < 50 && frame > 0) {
				frame = frame * (100 / frame);
			} else if (frame >= 50 && frame < 80) {
				frame = frame * (160 / frame);
			}

			sumframe += frame;
			System.out.println("frame=" + frame);
		}
		System.out.println("sumframe=" + sumframe);
		ActionFrame.add(sumframe);
		return EventIndividual;
	}

	// 添加的模型是否含有参照物
	public static Document isContainReference(OWLIndividual e, String maName, OWLModel model, Document doc) {
		boolean flage1 = false;
		OWLNamedClass AddModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty addModelNumber = model.getOWLDatatypeProperty("addModelNumber");
		OWLDatatypeProperty isTemplateObject = model.getOWLDatatypeProperty("isTemplateObject");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty addModelRelatedSpace = model.getOWLObjectProperty("addModelRelatedSpace");
		OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
		Collection AllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		OWLIndividual indi = null;
		OWLIndividual refe = null;
		for (Iterator in = AllAddPeopleIndividuals.iterator(); in.hasNext();) {
			indi = (OWLIndividual) in.next();
			refe = (OWLIndividual) indi.getPropertyValue(hasModelNameProperty);
			if (refe.getBrowserText().equals(e.getBrowserText())) {
				flage1 = true;
				break;
			}
		}
		if (flage1 == false) {
			int count = AddModelRelatedClass.getInstanceCount();
			count++;
			String str = "modelID" + count;
			refe = AddModelRelatedClass.createOWLIndividual(str);
			refe.addPropertyValue(addModelNumber, "1");
			refe.addPropertyValue(addModelTypeProperty, "model");
			refe.addPropertyValue(hasModelNameProperty, e);
			refe.addPropertyValue(isTemplateObject, "1");
			refe.addPropertyValue(modelIDProperty, str);
			OWLIndividual ind = model.getOWLIndividual(maName);
			String space = null;
			Collection col = ind.getPropertyValues(hasSceneSpace);
			if (col.size() != 0) {
				for (Iterator in = col.iterator(); in.hasNext();) {
					OWLIndividual ols = (OWLIndividual) in.next();
					OWLNamedClass nas = (OWLNamedClass) ols.getDirectType();
					String s = nas.getBrowserText().toString();
					if (s.contains("Ground") && !ols.getBrowserText().toString().contains("Floor")) {
						refe.addPropertyValue(addModelRelatedSpace, ols);
						space = ols.getBrowserText().toString();
						break;
					}
				}
			}
			OWLNamedClass supeclass = (OWLNamedClass) e.getDirectType();
			doc = WriteXML1(model, doc, str, e.getBrowserText(), supeclass.getBrowserText(), space, "model", "1", "");
		}
		return doc;
	}

	// 参照物，获取兄弟实例，随机选择
	public static OWLIndividual getSlibIndividual(OWLIndividual i, OWLModel model) {
		OWLNamedClass onc = (OWLNamedClass) i.getDirectType();
		Collection clo = onc.getInstances();
		ArrayList<OWLIndividual> owlin = new ArrayList<OWLIndividual>();
		if (clo.size() > 0) {
			for (Iterator in = clo.iterator(); in.hasNext();) {
				owlin.add((OWLIndividual) in.next());
			}
			int k = random(owlin.size());
			return owlin.get(k);
		} else
			return i;
	}

	// 得到与主事件相关的辅助事件
	public static ArrayList<OWLNamedClass> getEvent2(OWLNamedClass event, OWLModel model, String name) {

		ArrayList<OWLNamedClass> linkevent = new ArrayList<OWLNamedClass>();
		String str1 = "";
		OWLObjectProperty hasEvent = model.getOWLObjectProperty(str + name);
		ArrayList<String> eventclass = new ArrayList<String>();// 存储着事件的前驱事件的字符串
		RDFResource resource = event.getSomeValuesFrom(hasEvent);
		if (resource != null) {
			String hasValues = resource.getBrowserText();
			if (hasValues.indexOf("or") >= 0) {
				String[] hasValuesSplit = hasValues.split("or");
				if (hasValuesSplit.length > 0) {// 判断是否对应多个主题
					for (int i = 0; i < hasValuesSplit.length; i++) {
						eventclass.add(hasValuesSplit[i].toString().trim());
					}
				}
			} else {
				eventclass.add(resource.getBrowserText());
			}
		}
		for (Iterator it = eventclass.iterator(); it.hasNext();) {
			str1 = (String) it.next();
			linkevent.add(model.getOWLNamedClass(str1));
		}

		System.out.println(name.substring(3) + "=" + linkevent);
		return linkevent;

	}

	// 获得动作
	public static OWLIndividual getAction(OWLNamedClass e, OWLModel model) {
		ArrayList<String> actionclass = new ArrayList<String>();
		ArrayList<OWLNamedClass> action = new ArrayList<OWLNamedClass>();
		ArrayList<OWLIndividual> actindividual = new ArrayList<OWLIndividual>();
		OWLNamedClass eve = null;
		OWLNamedClass act = null;
		OWLIndividual acti = null;
		OWLObjectProperty hasSuitAction = model.getOWLObjectProperty(str + "hasSuitAction");
		RDFResource resource = e.getSomeValuesFrom(hasSuitAction);
		if (resource != null) {
			String hasValues = resource.getBrowserText();
			if (hasValues.indexOf("or") >= 0) {
				String[] hasValuesSplit = hasValues.split("or");
				if (hasValuesSplit.length > 0) {// 判断是否对应多个主题
					for (int i = 0; i < hasValuesSplit.length; i++) {
						actionclass.add(hasValuesSplit[i].toString().trim());
					}
				}
			} else {
				actionclass.add(resource.getBrowserText());
			}
		}
		for (Iterator is = actionclass.iterator(); is.hasNext();) {
			String s = (String) is.next();
			action.add(model.getOWLNamedClass(s));
		}
		if (action.size() != 0) {
			int k = random(action.size());
			act = action.get(k);
			if (act.getInstanceCount() != 0) {
				Collection actionind = act.getInstances();
				for (Iterator in = actionind.iterator(); in.hasNext();) {
					actindividual.add((OWLIndividual) in.next());
				}
				int i = random(actindividual.size());
				acti = actindividual.get(i);
			}
		}
		Collection col = e.getInstances();
		if (col.size() != 0) {
			for (Iterator i = col.iterator(); i.hasNext();) {
				OWLIndividual in = (OWLIndividual) i.next();
				in.setPropertyValue(hasSuitAction, acti);
			}
		} else {
			String str = e.getBrowserText().toString();
			OWLIndividual in = (OWLIndividual) e.createInstance(str + "1");
			in.setPropertyValue(hasSuitAction, acti);
		}
		System.out.println("action=" + acti);

		return acti;
	}

	public static String getifAddConstraint(OWLIndividual action, OWLModel model) {
		System.out.println(action);
		int i = 0;
		OWLDatatypeProperty ifAddConstraintProperty = model.getOWLDatatypeProperty("p2:ifAddConstraint");

		String ifAddConstraint = "0";
		Object s = action.getPropertyValue(ifAddConstraintProperty);
		System.out.println("数据:" + s.toString());
		if (action.getPropertyValue(ifAddConstraintProperty) != null) {
			ifAddConstraint = action.getPropertyValue(ifAddConstraintProperty).toString();
		}
		if (ifAddConstraint.equals("1"))
			ifAddConstraint = "true";
		else
			ifAddConstraint = "false";
		return ifAddConstraint;
	}

	public static int random(int n) {
		Random ran = new Random();
		int k = ran.nextInt(n);
		return k;
	}

	// 判断是否有人物没有主题
	public static Document addActionToModel(OWLModel model, Document doc, ArrayList<String> topiclist2,
			ArrayList list) {
		OWLNamedClass AddModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty topicname = model.getOWLDatatypeProperty("topicName");
		OWLObjectProperty hasSuitAction = model.getOWLObjectProperty(str + "hasSuitAction");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLDatatypeProperty isDeal = model.getOWLDatatypeProperty("isUsed");
		ArrayList<String> topiclist1 = new ArrayList<String>();
		int totalActionNum = 0;
		String[] actionList1 = new String[200]; // 用来存储动作
		int actFromTemNumber = 0;
		String topicName = "";
		ArrayList<String> actionList = new ArrayList<String>();
		OWLNamedClass actionClass = model.getOWLNamedClass("p2:Action");

		Collection clo = AddModelRelatedClass.getInstances();
		if (clo.size() != 0) {
			for (Iterator i = clo.iterator(); i.hasNext();) {
				OWLIndividual owli = (OWLIndividual) i.next();

				if (owli.getPropertyValue(addModelTypeProperty).equals("people")) {
					String isUsed = owli.getPropertyValue(isDeal).toString();
					if (isUsed.equals("false")) {
						boolean flage2 = true;
						Object topicn = owli.getPropertyValue(topicname);
						for (int m = 0; m < topiclist2.size(); m++) {
							if (topicn != null && topicn.equals(topiclist2.get(m))) {
								flage2 = false;
								break;
							}

						}
						if (flage2 == true) {
							if (topicn != null) {
								topiclist1.add(topicn.toString());
							} else {
								topiclist1 = topiclist2;
							}
							for (Iterator itTopic = topiclist1.iterator(); itTopic.hasNext();) {
								topicName = (String) itTopic.next();
								System.out.println("topicName:" + topicName);
								OWLNamedClass topic = model.getOWLNamedClass(topicName);
								Collection ActionSubClass = actionClass.getSubclasses(true);
								OWLObjectProperty actionSuitableForTopicProperty = model
										.getOWLObjectProperty("p2:actionSuitableForTopic");
								Collection subclassIndiviual = null;
								for (Iterator it = ActionSubClass.iterator(); it.hasNext();) {
									OWLNamedClass subclass = (OWLNamedClass) it.next();
									if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
										continue;
									}
									String hasTopicClassType = (subclass
											.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
									if (hasTopicClassType
											.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
										OWLUnionClass hasTopicUnion = (OWLUnionClass) subclass
												.getSomeValuesFrom(actionSuitableForTopicProperty);
										Collection hasTopic_collection = hasTopicUnion.getNamedOperands();
										for (Iterator jm = hasTopic_collection.iterator(); jm.hasNext();) {
											OWLNamedClass hasTopicClass = (OWLNamedClass) jm.next();
											if (hasTopicClass.equalsStructurally(topic)) {
												subclassIndiviual = subclass.getInstances(true);
												for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
													OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
													String actionName = actionIndiviual.getBrowserText();
													actionList.add(actionName);
												}
												continue;
											}
										}
									} else {
										OWLNamedClass classname = (OWLNamedClass) subclass
												.getSomeValuesFrom(actionSuitableForTopicProperty);
										if (classname == null)
											continue;
										if (classname.equalsStructurally(topic)) {
											subclassIndiviual = subclass.getInstances(true);
											for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
												OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
												String actionName = actionIndiviual.getBrowserText();
												actionList.add(actionName);
											}
											continue;
										}
									}
								}
							}

							if (actionList.size() != 0) {
								System.out.println("根据主题抽取到" + actionList.size() + "个动作");
								actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
								totalActionNum = actionList.size();
							} else {
								/*
								 * 处理处理模板信息
								 */
								OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty("p2:mapToAction");
								int listSize = list.size();
								System.out.print(listSize + "\n");
								String[] listToStr = new String[listSize]; // 将arraylist转化为string[]
								String[] templateList = new String[listSize]; // 处理后的template信息
								if (listSize != 0) {
									listToStr = (String[]) list.toArray(new String[listSize]);// 增加了(String[])
									for (int i1 = 0; i1 < listToStr.length; i1++) {
										String str2 = listToStr[i1];
										int pos = str2.indexOf(":");
										templateList[i1] = (String) str2.subSequence(pos + 1, str2.length());
										System.out.println("templateList[" + i1 + "]=" + templateList[i1]);
									}
								}
								if (templateList.length > 0) {
									for (int i1 = 0; i1 < templateList.length; i1++) {
										OWLIndividual actTemIndividual = model.getOWLIndividual(templateList[i1]);
										System.out.println("tempalteList[" + i1 + "]=" + templateList[i1]);
										Collection mapToActionValues = actTemIndividual
												.getPropertyValues(mapToActionProperty);
										if (!mapToActionValues.isEmpty()) {
											for (Iterator it1 = mapToActionValues.iterator(); it1.hasNext();) {
												OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
												actionList1[actFromTemNumber] = actionIndiviual.getBrowserText();
												System.out.println(actionList1[actFromTemNumber]);
												actFromTemNumber++;
											}
										}
									}
								}
								if (actFromTemNumber != 0) {
									totalActionNum = actFromTemNumber;
									System.out.println("没有主题，根据原子共抽取到" + actFromTemNumber + "个动作");
								}
							}
							// 在不能挑出动作的情况下，随机加动作
							if (actionList.size() == 0 && actFromTemNumber == 0) {
								System.out.println("主题与原子没有抽取到合适的动作,若有模型，会随机加动作");
								// 没有抽出动作，随机加动作
								int actionNum = 0;
								ArrayList actionlist = new ArrayList();
								actionlist.add("WalkAction");
								actionlist.add("RunAction");
								actionlist.add("WaitAction");
								for (int i1 = 0; i1 < actionlist.size(); i1++) {
									String s = (String) actionlist.get(i1);
									OWLNamedClass RandomClass = model.getOWLNamedClass("p2:" + s);
									Collection actionAllIndividuals = RandomClass.getInstances(true);
									for (Iterator it = actionAllIndividuals.iterator(); it.hasNext();) {
										OWLIndividual actionIndiviual = (OWLIndividual) it.next();
										String actionName = actionIndiviual.getBrowserText();
										actionList1[actionNum] = actionName;
										actionNum++;
									}
									totalActionNum = actionNum;
								}
							}
							String id = owli.getPropertyValue(modelIDProperty).toString();
							System.out.println("modelId=" + id);

							OWLIndividual modelin = (OWLIndividual) owli.getPropertyValue(hasModelNameProperty);
							String modelname = modelin.getBrowserText().toString();

							int rd = (int) (Math.random() * totalActionNum);// 用来产生随机数，判断加哪个动作

							int pos1 = actionList1[rd].indexOf(":");
							String finalActionName = (String) actionList1[rd].subSequence(pos1 + 1,
									actionList1[rd].length());
							System.out.println("添加动作名称；" + finalActionName);
							// 2014.6.18修改--------------
							OWLIndividual finalAddactionindivadual = model.getOWLIndividual(actionList1[rd]);
							OWLDatatypeProperty ifAddConstraintProperty = model
									.getOWLDatatypeProperty("p2:ifAddConstraint");
							String ifAddConstraint = "0";
							System.out
									.println("数据" + finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty));
							if (finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty) != null) {
								ifAddConstraint = finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty)
										.toString();
							}

							if (ifAddConstraint.equals("1")) {
								OWLDatatypeProperty constraintTypeProperty = model
										.getOWLDatatypeProperty("p2:actionConstraintType");
								String constraintType;
								if (finalAddactionindivadual.getPropertyValue(constraintTypeProperty) != null) {
									constraintType = finalAddactionindivadual.getPropertyValue(constraintTypeProperty)
											.toString();
								} else {
									constraintType = "default";
								}

								OWLObjectProperty relativeModelProperty = model
										.getOWLObjectProperty("p2:modelSuitableForAction");
								OWLIndividual relativeModelIndividual = null;
								String relativeModel;
								if (finalAddactionindivadual.getPropertyValue(relativeModelProperty) != null) {
									relativeModelIndividual = (OWLIndividual) finalAddactionindivadual
											.getPropertyValue(relativeModelProperty);
									relativeModel = relativeModelIndividual.getBrowserText();
								} else {
									relativeModel = "default";
								}

								OWLDatatypeProperty modelRelativePositionProperty = model
										.getOWLDatatypeProperty("p2:actionRelativeModelPosition");
								String relativeModelPosition;
								if (finalAddactionindivadual.getPropertyValue(modelRelativePositionProperty) != null) {
									relativeModelPosition = finalAddactionindivadual
											.getPropertyValue(modelRelativePositionProperty).toString();
								} else {
									relativeModelPosition = "default";
								}

								ifAddConstraint = "ture";
							} else {
								ifAddConstraint = "false";
							}

							// 标志位，代表该人物已经经过交互动作所处理20170617
							owli.setPropertyValue(isDeal, "true");
							String str1 = null;

							OWLIndividual actionname1 = model.getOWLIndividual("p2:" + finalActionName);
							OWLNamedClass owln = (OWLNamedClass) actionname1.getDirectType();
							OWLNamedClass eventclass = model.getOWLNamedClass(str + "Event");
							Collection coll = eventclass.getSubclasses(true);
							for (Iterator ir = coll.iterator(); ir.hasNext();) {
								OWLNamedClass sub = (OWLNamedClass) ir.next();

								RDFResource resource = sub.getSomeValuesFrom(hasSuitAction);
								if (resource != null) {
									String hasValues = resource.getBrowserText();
									if (hasValues.indexOf("or") > 0) {
										String[] hasValuesSplit = hasValues.split("or");
										for (int ik = 0; ik < hasValuesSplit.length; ik++) {
											if (hasValuesSplit[ik].equals(owln.getBrowserText().toString())) {
												str1 = sub.getBrowserText().toString();
												break;
											}
										}

									} else {

										if (hasValues.equals(owln.getBrowserText().toString())) {

											str1 = sub.getBrowserText().toString();

											break;
										}

									}

								}

							}
							System.out.println("单个动作对应的事件名称=" + str1);
							if (str1 != null && str1.length() != 0) {
								str1 = str1.substring(str1.indexOf(":") + 1);
							}

							flage.add("true");
							doc = WriteXML(model, doc, str1, id, modelname, finalActionName, "default", "null",
									ifAddConstraint);

						}
					}
				}

			}
		}

		return doc;
	}

	public static int getFrame(OWLModel model, OWLIndividual action) {
		int frame = 0;
		OWLDatatypeProperty maFrameNumber = model.getOWLDatatypeProperty("maFrameNumber");

		frame = (Integer) action.getPropertyValue(maFrameNumber);
		return frame;
	}

	// 修改ma的帧数
	public static Document changeMaFrame(OWLModel model, String maName, Document doc) {

		OWLIndividual maindividual = model.getOWLIndividual(maName);
		OWLDatatypeProperty maFrameNumber = model.getOWLDatatypeProperty("maFrameNumber");
		int max = 0;

		if (ActionFrame.size() != 0) {
			for (int i = 0; i < ActionFrame.size(); i++) {
				int s = (Integer) ActionFrame.get(i);
				if (max < s)
					max = s;
			}
		}
		int frame = getFrame(model, maindividual);
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		if (max != 0) {
			frame = max;
			maindividual.removePropertyValue(maFrameNumber, frame);
			maindividual.setPropertyValue(maFrameNumber, frame);

		}

		name.addAttribute("maFrame", String.valueOf(frame));
		String fare = name.attributeValue("maFrame");
		int frame1 = Integer.parseInt(fare);
		System.out.println(frame1);

		System.out.println("maFrame=" + frame);
		return doc;
	}

	public static OWLIndividual setReference(OWLModel model) {
		OWLIndividual reference = null;
		ArrayList Outdoortreelist = new ArrayList();
		ArrayList Indoortreelist = new ArrayList();
		OWLNamedClass tree = model.getOWLNamedClass("Flowers");
		OWLIndividual maname = model.getOWLIndividual(name);
		OWLObjectProperty Location = model.getOWLObjectProperty("Location");
		OWLObjectProperty hasValueOfPlace = model.getOWLObjectProperty("hasValueOfPlace");
		Collection treecol = tree.getSubclasses(true);
		for (Iterator it = treecol.iterator(); it.hasNext();) {
			OWLNamedClass treesub = (OWLNamedClass) it.next();
			if (treesub.getInstanceCount() != 0) {
				Collection treeIndi = treesub.getInstances();
				System.out.println(treeIndi.size());
				for (Iterator ite = treeIndi.iterator(); ite.hasNext();) {
					OWLIndividual tre = (OWLIndividual) ite.next();
					System.out.println(tre.getBrowserText());
					if (tre.getPropertyValueCount(Location) == 1) {
						OWLIndividual loca = (OWLIndividual) tre.getPropertyValue(Location);
						System.out.println(loca);
						if (loca.getBrowserText().equals("OutdoorOnLandLocation")) {
							Outdoortreelist.add(tre);
						} else if (loca.getBrowserText().equals("IndoorOnLandLocation")) {
							Indoortreelist.add(tre);
						}
					} else if (tre.getPropertyValueCount(Location) > 1) {
						Collection clo = tre.getPropertyValues(Location);
						for (Iterator in = clo.iterator(); in.hasNext();) {
							OWLIndividual ob = (OWLIndividual) in.next();
							if (ob.getBrowserText().equals("OutdoorOnLandLocation")) {
								Outdoortreelist.add(tre);
							} else if (ob.getBrowserText().equals("IndoorOnLandLocation")) {
								Indoortreelist.add(tre);
							}
						}
					}
				}
			}
		}
		OWLIndividual place = (OWLIndividual) maname.getPropertyValue(hasValueOfPlace);
		int k = 0;
		if (place.getBrowserText().equals("outDoorDescription")) {
			k = random(Outdoortreelist.size());
			reference = (OWLIndividual) Outdoortreelist.get(k);
		} else if (place.getBrowserText().equals("inDoorDescription")) {
			k = random(Indoortreelist.size());
			reference = (OWLIndividual) Outdoortreelist.get(k);
		}
		return reference;
	}

	private static int Interger(String s) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static OWLModel createOWLFile1(String url) throws OntologyLoadException {
		try {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;
		} catch (Exception zz) {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;
		}
	}

	public static void main(String args[]) throws OntologyLoadException, IOException {
		String url = "file:///C://ontologyOWL/sumoOWL2/sumo_phone3.owl";
		OWLModel model = createOWLFile1(url);// 通过url获得owl模型
		ArrayList<String> englishTopic = new ArrayList();
		ArrayList<String> topic = new ArrayList();
		ArrayList<String> action = new ArrayList();
		// topic.add("踢足球");
		// topic.add("打篮球");
		topic.add("悲");
		topic.add("喜悦");

		ArrayList<String> topiclist = new ArrayList();
		OWLNamedClass topicn = model.getOWLNamedClass("Topic");
		OWLDatatypeProperty chineseTopic = model.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls = null;
		Collection clo = topicn.getSubclasses(true);
		for (int i = 0; i < topic.size(); i++) {

			for (Iterator in = clo.iterator(); in.hasNext();) {
				cls = (OWLNamedClass) in.next();
				Object hasvali = cls.getHasValue(chineseTopic);
				if (hasvali != null && topic.get(i).equals(hasvali.toString())) {
					topiclist.add(cls.getBrowserText().toString());
				}
			}
		}
		System.out.println("topiclist=" + topiclist);
		String xmlPath = "PlotDataOut/test1.xml";
		Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		// doc=addActionToModel(model, doc,topiclist);
		doc = EventInfer(topiclist, action, model, "Tropical45.ma", doc);
		XMLWriter writer = new XMLWriter(new FileWriter("PlotDataOut/test1.xml"));
		writer.write(doc);
		writer.close();

	}

}
