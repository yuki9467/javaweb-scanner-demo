package com.demo;

@CustomAnnotationBean
public class T2 {
	private String id;

	private String name;

	@CustomAnnotationMethod(uri = "t2/getId")
	public String getId() {
		System.out.println("进入==========t2/getId=========方法");
		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	@CustomAnnotationMethod(uri = "t2/getName")
	public String getName() {

		System.out.println("进入==========t2/getName=========方法");
		return name;
	}

	public void setName(String name) {

		this.name = name;
	}
}
