import chardet
import os

# 检查指定目录下的HTML文件编码
def check_encoding(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.html') or file.endswith('.htm'):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'rb') as f:
                        raw_data = f.read()
                        result = chardet.detect(raw_data)
                        encoding = result['encoding']
                        confidence = result['confidence']
                        print(f"文件: {file_path}")
                        print(f"编码: {encoding}, 置信度: {confidence}")
                        print("-" * 50)
                except Exception as e:
                    print(f"处理文件 {file_path} 时出错: {e}")

if __name__ == "__main__":
    # 检查webapp目录
    check_encoding('d:\\1\\space-knowledge-platform\\src\\main\\webapp')
