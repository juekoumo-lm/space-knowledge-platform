import chardet
import os

# 检查指定目录下的文件编码
def check_encoding(directory):
    total_files = 0
    utf8_files = 0
    non_utf8_files = []
    
    for root, dirs, files in os.walk(directory):
        # 跳过target目录
        if 'target' in root:
            continue
        for file in files:
            if file.endswith(('.html', '.htm', '.java', '.xml', '.properties', '.sql')):
                file_path = os.path.join(root, file)
                total_files += 1
                try:
                    with open(file_path, 'rb') as f:
                        raw_data = f.read()
                        result = chardet.detect(raw_data)
                        encoding = result['encoding']
                        confidence = result['confidence']
                        
                        if encoding and 'utf' in encoding.lower():
                            utf8_files += 1
                        else:
                            non_utf8_files.append((file_path, encoding, confidence))
                except Exception as e:
                    non_utf8_files.append((file_path, 'Error', str(e)))
    
    print(f"总文件数: {total_files}")
    print(f"UTF-8编码文件数: {utf8_files}")
    print(f"非UTF-8编码文件数: {len(non_utf8_files)}")
    
    if non_utf8_files:
        print("\n非UTF-8编码文件:")
        for file_path, encoding, confidence in non_utf8_files:
            print(f"{file_path} - 编码: {encoding}, 置信度: {confidence}")
    else:
        print("\n所有文件都是UTF-8编码!")

if __name__ == "__main__":
    # 检查项目根目录
    check_encoding('d:\\1\\space-knowledge-platform')
