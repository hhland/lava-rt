package com.sohu.sohudb;

/**
 * SOHU DB����������
 * @author Administrator
 *
 */
public enum RequestType {

	CMD_UNKNOWN,// ��֧�ֵ�����
	CMD_GET,   // ��ȡ����
	CMD_PUT,   // д����
	CMD_DEL,   // ɾ������
	CMD_SYNC,  // ����ͬ���ź�
	CMD_QGET,  // ��δ֪��
	CMD_GETSIZE, //��δ֪��
	CMD_GETMETA, //��δ֪��
}
